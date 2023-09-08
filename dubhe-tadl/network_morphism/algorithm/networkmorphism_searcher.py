import logging
import os
import shutil
from utils import Constant, OptimizeMode
from .bayesian import BayesianOptimizer
from .nn import CnnGenerator, ResNetGenerator, MlpGenerator
from .graph import graph_to_json, json_to_graph

logger = logging.getLogger(__name__)

class NetworkMorphismSearcher:
    """
    NetworkMorphismSearcher is a tuner which using network morphism techniques.

    Attributes
    ----------
    n_classes : int
        The class number or output node number (default: ``10``)
    input_shape : tuple
        A tuple including: (input_width, input_width, input_channel)
    t_min : float
        The minimum temperature for simulated annealing. (default: ``Constant.T_MIN``)
    beta : float
        The beta in acquisition function. (default: ``Constant.BETA``)
    algorithm_name : str
        algorithm name used in the network morphism (default: ``"Bayesian"``)
    optimize_mode : str
        optimize mode "minimize" or "maximize" (default: ``"minimize"``)
    verbose : bool
        verbose to print the log (default: ``True``)
    bo : BayesianOptimizer
        The optimizer used in networkmorphsim tuner.
    max_model_size : int
        max model size to the graph (default: ``Constant.MAX_MODEL_SIZE``)
    default_model_len : int
        default model length (default: ``Constant.MODEL_LEN``)
    default_model_width : int
        default model width (default: ``Constant.MODEL_WIDTH``)
    search_space : dict
    """

    def __init__(
            self,
            path,
            best_selected_space_path,
            task="cv",
            input_width=32,
            input_channel=3,
            n_output_node=10,
            algorithm_name="Bayesian",
            optimize_mode="maximize",
            verbose=True,
            beta=Constant.BETA,
            t_min=Constant.T_MIN,
            max_model_size=Constant.MAX_MODEL_SIZE,
            default_model_len=Constant.MODEL_LEN,
            default_model_width=Constant.MODEL_WIDTH,
    ):
        """
        initilizer of the NetworkMorphismSearcher.
        """
        self.path = path
        self.best_selected_space_path = best_selected_space_path

        if task == "cv":
            self.generators = [CnnGenerator]
        elif task == "common":
            self.generators = [MlpGenerator]
        else:
            raise NotImplementedError(
                '{} task not supported in List ["cv","common"]')

        self.n_classes = n_output_node
        self.input_shape = (input_width, input_width, input_channel)

        self.t_min = t_min
        self.beta = beta
        self.algorithm_name = algorithm_name
        self.optimize_mode = OptimizeMode(optimize_mode)
        self.json = None
        self.total_data = {}
        self.verbose = verbose

        self.bo = BayesianOptimizer(
            self, self.t_min, self.optimize_mode, self.beta)

        self.training_queue = []
        self.descriptors = []
        self.history = []

        self.max_model_size = max_model_size
        self.default_model_len = default_model_len
        self.default_model_width = default_model_width

    def search(self, parameter_id, args):
        """
        Returns a set of trial neural architecture, as a serializable object.

        Parameters
        ----------
        parameter_id : int
        """
        if not self.history:
            self.init_search(args)

        new_father_id = None
        generated_graph = None
        # 先看training queue里面有没有元素，有就是init_search了
        # 如果有history了话那就生成一个
        if not self.training_queue:
            new_father_id, generated_graph = self.generate()
            new_model_id = args.trial_id
            self.training_queue.append(
                (generated_graph, new_father_id, new_model_id))
            
            self.descriptors.append(generated_graph.extract_descriptor())

        graph, father_id, model_id = self.training_queue.pop(0)

        # from graph to json
        json_out = graph_to_json(graph, os.path.join(self.path, str(model_id),'model_selected_space.json'))
        self.total_data[parameter_id] = (json_out, father_id, model_id)

        return json_out

    def update_searcher(self, parameter_id, value, **kwargs):
        """
        Record an observation of the objective function.

        Parameters
        ----------
        parameter_id : int
            the id of a group of paramters that generated by nni manager.
        value : dict/float
            if value is dict, it should have "default" key.
        """

        if parameter_id not in self.total_data:
            raise RuntimeError("Received parameter_id not in total_data.")

        (_, father_id, model_id) = self.total_data[parameter_id]

        graph = self.bo.searcher.load_model_by_id(model_id)

        # to use the value and graph
        self.add_model(value, model_id)
        self.update(father_id, graph, value, model_id)


    def init_search(self,args):
        """
        Call the generators to generate the initial architectures for the search.
        """
        if self.verbose:
            logger.info("Initializing search.")
        for generator in self.generators:
            graph = generator(self.n_classes, self.input_shape).generate(
                self.default_model_len, self.default_model_width
            )
            model_id = args.trial_id
            self.training_queue.append((graph, -1, model_id))
            self.descriptors.append(graph.extract_descriptor())

        if self.verbose:
            logger.info("Initialization finished.")


    def generate(self):
        """
        Generate the next neural architecture.

        Returns
        -------
        other_info : any object
            Anything to be saved in the training queue together with the architecture.
        generated_graph : Graph
            An instance of Graph.
        """
        generated_graph, new_father_id = self.bo.generate(self.descriptors)
        if new_father_id is None:
            new_father_id = 0
            generated_graph = self.generators[0](
                self.n_classes, self.input_shape
            ).generate(self.default_model_len, self.default_model_width)

        return new_father_id, generated_graph

    def update(self, other_info, graph, metric_value, model_id):
        """
        Update the controller with evaluation result of a neural architecture.

        Parameters
        ----------
        other_info: any object
            In our case it is the father ID in the search tree.
        graph: Graph
            An instance of Graph. The trained neural architecture.
        metric_value: float
            The final evaluated metric value.
        model_id: int
        """
        father_id = other_info
        self.bo.fit([graph.extract_descriptor()], [metric_value])
        self.bo.add_child(father_id, model_id)

    def add_model(self, metric_value, model_id):
        """
        Add model to the history, x_queue and y_queue

        Parameters
        ----------
        metric_value : float
        graph : dict
        model_id : int

        Returns
        -------
        model : dict
        """
        if self.verbose:
            logger.info("Saving model.")

        # Update best_model text file
        ret = {"model_id": model_id, "metric_value": metric_value}
        self.history.append(ret)
        # update best selected space
        if model_id == self.get_best_model_id():
            best_model_path = os.path.join(self.path, str(model_id),'model_selected_space.json')
            shutil.copy(best_model_path, self.best_selected_space_path)
        return ret


    def get_best_model_id(self):
        """
        Get the best model_id from history using the metric value
        """

        if self.optimize_mode is OptimizeMode.Maximize:
            return max(self.history, key=lambda x: x["metric_value"])[
                "model_id"]
        return min(self.history, key=lambda x: x["metric_value"])["model_id"]


    def load_model_by_id(self, model_id):
        """
        Get the model by model_id

        Parameters
        ----------
        model_id : int
            model index

        Returns
        -------
        load_model : Graph
            the model graph representation
        """

        with open(os.path.join(self.path, str(model_id), "model_selected_space.json")) as fin:
            json_str = fin.read().replace("\n", "")

        load_model = json_to_graph(json_str)
        return load_model

    def load_best_model(self):
        """
        Get the best model by model id

        Returns
        -------
        load_model : Graph
            the model graph representation
        """
        return self.load_model_by_id(self.get_best_model_id())

    def get_metric_value_by_id(self, model_id):
        """
        Get the model metric valud by its model_id

        Parameters
        ----------
        model_id : int
            model index

        Returns
        -------
        float
             the model metric
        """
        for item in self.history:
            if item["model_id"] == model_id:
                return item["metric_value"]
        return None