import numpy as np
import enum, h5py


class ActivationFunction(enum.Enum):
    SIGMOID = 1
    TANH = 2
    RELU = 3
    LEAKY_RELU = 4
    SOFTMAX = 5
    LINEAR = 6

class LossFunction(enum.Enum):
    BCE = 1
    MSE = 2
    MAE = 3
    CROSSENTROPY = 4

class Optimizer(enum.Enum):
    SGD = 1
    ADAM = 2
    SGD_MOMENTUM = 3
    ADAGRAD = 4
    RMSPROP = 5

class Initializer(enum.Enum):
    HE = 1
    XAVIER = 2
    NORMAL = 3
    UNIFORM = 4
    RANDOM = 5

class Regularizer(enum.Enum):
    L1 = 1
    L2 = 2
    L1_L2 = 3

class LayerType(enum.Enum):
    DENSE = 1
    FLATTEN = 2
    DROPOUT = 3

class Layer:
    def __init__(self, type, shape, activation_function):
        self.type = type
        self.shape = shape
        self.activation_function = activation_function
        self.units = []

class Dense(Layer):
    def __init__(self, units, activation_function):
        super().__init__(LayerType.DENSE, (1, units), activation_function)

class Flatten(Layer):
    def __init__(self, shape):
        super().__init__(LayerType.FLATTEN, shape, ActivationFunction.LINEAR)

class Dropout:
    def __init__(self, rate):
        self.rate = rate


class ActivationFunctions:
    @staticmethod
    def sigmoid(x):
        return 1 / (1 + np.exp(-x))

    @staticmethod
    def tanh(x):
        return np.tanh(x)

    @staticmethod
    def relu(x):
        return np.maximum(0, x)

    @staticmethod
    def leaky_relu(x):
        return np.maximum(0.01 * x, x)

    @staticmethod
    def softmax(x):
        return np.exp(x) / np.sum(np.exp(x), axis=1, keepdims=True)

    @staticmethod
    def linear(x):
        return x
    
class Neuron:
    def __init__(self, activation, weights, bias):
        self.bias = bias
        self.weights = weights
        self.input = []
        self.output = 0
        self.activation = activation

    def compute(self, input):
        self.input = input
        self.output = self.activation(np.dot(input, self.weights) + self.bias)
        return self.output
    

class Model:
    def __init__(self, loss_function, optimizer, initializer, regularizer):
        self.layers = []
        self.loss_function = loss_function
        self.optimizer = optimizer
        self.initializer = initializer
        self.regularizer = regularizer

    def fit(self, x_train, y_train, epochs, batch_size):
        pass

    def predict(self, x_test):
        pass

    def evaluate(self, x_test, y_test):
        pass

    def summary(self):
        pass

    def add(self, layer):
        self.layers.append(layer)

    def compile(self):
        if self.initializer == Initializer.HE:
            self.__initialization_HE()
        elif self.initializer == Initializer.XAVIER:
            self.__initialization_XAVIER()
        elif self.initializer == Initializer.NORMAL:
            self.__initialization_NORMAL()
        elif self.initializer == Initializer.UNIFORM:
            self.__initialization_UNIFORM()
        elif self.initializer == Initializer.RANDOM:
            self.__initialization_RANDOM()
        else:
            raise Exception(f'Invalid initializer: {self.initializer}')


    def __initialization_HE(self):
        for i, layer in enumerate(self.layers):
            #for 
            if i == 0:
                weights = np.random.randn(layer.shape[0], layer.shape[1]) * np.sqrt(2 / layer.shape[1])
                self.layers[i].units.append(Neuron(layer.activation_function, weights, 1))
            else:
                weights = np.random.randn(layer.shape[0], layer.shape[1]) * np.sqrt(2 / self.layers[i-1].shape[1])
                self.layers[i].units.append(Neuron(layer.activation_function, weights, 1))

    def __initialization_XAVIER(self):
        pass

    def __initialization_NORMAL(self):
        pass

    def __initialization_UNIFORM(self):
        pass

    def __initialization_RANDOM(self):
        pass
    

    def save(self, path):
        with h5py.File(path, 'w') as f:
            for i, layer in enumerate(self.layers):
                f.create_dataset('layer' + str(i), data=layer)
            
            f.create_dataset('loss_function', data=self.loss_function)
            f.create_dataset('optimizer', data=self.optimizer)
            f.create_dataset('initializer', data=self.initializer)
            f.create_dataset('regularizer', data=self.regularizer)

            for i, (w, b) in enumerate(self.weights):
                f.create_dataset('weights' + str(i), data=w)
                f.create_dataset('bias' + str(i), data=b)

    def load(self, path):
        with h5py.File(path, 'r') as f:
            for i, layer in enumerate(self.layers):
                layer = f['layer' + str(i)].value
                self.layers.append(layer)

            self.loss_function = f['loss_function'].value
            self.optimizer = f['optimizer'].value
            self.initializer = f['initializer'].value
            self.regularizer = f['regularizer'].value

            for i, layer in enumerate(self.layers):
                w = f['weights' + str(i)].value
                b = f['bias' + str(i)].value
                self.weights.append((w, b))




