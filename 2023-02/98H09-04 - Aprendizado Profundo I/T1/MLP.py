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

class Regularizer(enum.Enum):
    L1 = 1
    L2 = 2
    L1_L2 = 3

class LayerType(enum.Enum):
    DENSE = 1
    FLATTEN = 2
    DROPOUT = 3

class Layer:
    def __init__(self, type, shape, activation_function, initializer):
        self.type = type
        self.shape = shape
        self.unit_count = shape[0] * shape[1]
        self.activation_function = activation_function
        self.units = []
        self.initializer = initializer
'''
class Dense(Layer):
    def __init__(self, units, activation_function):
        super().__init__(LayerType.DENSE, (1, units), activation_function)

class Flatten(Layer):
    def __init__(self, shape):
        super().__init__(LayerType.FLATTEN, shape, ActivationFunction.LINEAR)

class Dropout:
    def __init__(self, rate):
        self.rate = rate
'''

class ActivationFunctions:
    @staticmethod
    def sigmoid(x):
        return 1 / (1 + np.exp(-x))
    
    @staticmethod
    def sigmoid_derivative(x):
        f = ActivationFunctions.sigmoid(x)
        return f * (1 - f)

    @staticmethod
    def tanh(x):
        return np.tanh(x)
    
    @staticmethod
    def tanh_derivative(x):
        return 1 - np.tanh(x)**2

    @staticmethod
    def relu(x):
        return np.maximum(0, x)
    
    @staticmethod
    def relu_derivative(x):
        return np.where(x > 0, 1, 0)

    @staticmethod
    def leaky_relu(x):
        return np.maximum(0.01 * x, x)
    
    @staticmethod
    def leaky_relu_derivative(x, alpha=0.01):
        return np.where(x > 0, 1, alpha)

    @staticmethod
    def softmax(x):
        return np.exp(x) / np.sum(np.exp(x), axis=1, keepdims=True)
    
    @staticmethod
    def softmax_derivative(x):
        f = ActivationFunctions.softmax(x)
        return f * (1 - f)

    @staticmethod
    def linear(x):
        return x
    
    @staticmethod
    def linear_derivative(x):
        return np.ones_like(x)
    
    
class LossFunctions:
    @staticmethod
    def bce(y, y_hat):
        return -np.mean(y * np.log(y_hat) + (1-y) * np.log(1-y_hat))

    @staticmethod
    def mse(y, y_hat):
        return np.mean((y - y_hat)**2)

    @staticmethod
    def mae(y, y_hat):
        return np.mean(np.abs(y - y_hat))

    @staticmethod
    def crossentropy(y, y_hat):
        return -np.mean(y * np.log(y_hat))
    
class Optimizers:
    @staticmethod
    def sgd(learning_rate, weights, bias, grad_weights, grad_bias):
        weights -= learning_rate * grad_weights
        bias -= learning_rate * grad_bias
        return weights, bias

    @staticmethod
    def adam(learning_rate, weights, bias, grad_weights, grad_bias, m, v, t, beta1=0.9, beta2=0.999, epsilon=1e-8):
        t += 1
        m = beta1 * m + (1 - beta1) * grad_weights
        v = beta2 * v + (1 - beta2) * grad_bias**2
        m_corrected = m / (1 - beta1**t)
        v_corrected = v / (1 - beta2**t)
        weights -= learning_rate * m_corrected / (np.sqrt(v_corrected) + epsilon)
        bias -= learning_rate * m_corrected / (np.sqrt(v_corrected) + epsilon)
        return weights, bias, m, v, t

    @staticmethod
    def sgd_momentum(learning_rate, weights, bias, grad_weights, grad_bias, beta=0.9):
        pass

    @staticmethod
    def adagrad(learning_rate, weights, bias, grad_weights, grad_bias):
        pass

    @staticmethod
    def rmsprop(learning_rate, weights, bias, grad_weights, grad_bias, beta=0.9, epsilon=1e-8):
        pass

    
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
    def __init__(self, loss_function, optimizer, regularizer, dropout_rate=0.0):
        self.layers = []
        self.loss_function = loss_function
        self.optimizer = optimizer
        self.regularizer = regularizer
        self.dropout_rate = dropout_rate

    def fit(self, x_train, y_train, epochs, batch_size):

        pass

    def __forward(self, x):
        pass

    def __backward(self, y):
        pass

    def predict(self, x_test):
        pass

    def evaluate(self, x_test, y_test):
        pass

    def summary(self):
        #print summary of the model
        #print number of layers
        print('Number of layers: ', len(self.layers))

        #print number of units per layer
        units = 0
        for i, layer in enumerate(self.layers):
            units += layer.shape[0] * layer.shape[1]
        
        print('Units: ', units)

        #print number of parameters
        parameters = 0
        for i, layer in enumerate(self.layers):
            if layer.type == LayerType.FLATTEN:
                parameters += 0
            else:
                parameters += len(layer.units) # bias
                parameters += (layer.units[0].weights.shape[0] * layer.units[0].weights.shape[1]) * len(layer.units)

        print('Number of parameters: ', parameters)
        
        #print loss function
        print('Loss function: ', self.loss_function)        
        
        #print optimizer
        print('Optimizer: ', self.optimizer)        
                
        #print regularizer
        print('Regularizer: ', self.regularizer)        
        
        #print number of neurons per layer
        for i, layer in enumerate(self.layers):
            print('Initializer: ', layer.initializer)
            print('Layer ', i, layer.shape[0] * layer.shape[1], ' neurons')

        #print neurons per layer
        for i, layer in enumerate(self.layers):
            print('Layer ', i, layer.shape[1], ' neurons')
            for j, neuron in enumerate(layer.units):
                print('\tNeuron ', j, neuron.weights.shape, ' weights')
                print('\tWeights ', neuron.weights)


    def add(self, layer):
        self.layers.append(layer)

    def compile(self):
        for i, layer in enumerate(self.layers):
            if layer.initializer == Initializer.HE:
                self.__initialization_HE(i, layer)
            elif layer.initializer == Initializer.XAVIER:
                self.__initialization_XAVIER(i, layer)
            elif layer.initializer == Initializer.NORMAL:
                self.__initialization_NORMAL(i, layer)
            elif layer.initializer == Initializer.UNIFORM:
                self.__initialization_UNIFORM(i, layer)
            else:
                raise Exception(f'Invalid initializer: {self.initializer}')


    def __initialization_HE(self, i, layer):
        for a in range(layer.shape[0]):
            for b in range(layer.shape[1]):
                if i == 0:
                    weights = np.ones((1,1))
                else:
                    unit_count = self.layers[i-1].unit_count
                    weights = np.random.randn(self.layers[i-1].shape[0], self.layers[i-1].shape[1]) * np.sqrt(2 / unit_count)
                    
                self.layers[i].units.append(Neuron(layer.activation_function, weights, 1))

    def __initialization_XAVIER(self, i, layer):
        for a in range(layer.shape[0]):
            for b in range(layer.shape[1]):
                if i == 0:
                    weights = np.ones((1,1))
                else:
                    unit_count = self.layers[i-1].unit_count + layer.unit_count
                    weights = np.random.randn(self.layers[i-1].shape[0], self.layers[i-1].shape[1]) * np.sqrt(2 / unit_count)
                    
                self.layers[i].units.append(Neuron(layer.activation_function, weights, 1))

    def __initialization_NORMAL(self, i, layer):
        for a in range(layer.shape[0]):
            for b in range(layer.shape[1]):
                if i == 0:
                    weights = np.ones((1,1))
                else:
                    weights = np.random.randn(self.layers[i-1].shape[0], self.layers[i-1].shape[1]) * np.sqrt(0.01)
                
                self.layers[i].units.append(Neuron(layer.activation_function, weights, 1))

    def __initialization_UNIFORM(self, i, layer):
        for a in range(layer.shape[0]):
            for b in range(layer.shape[1]):
                if i == 0:
                    weights = np.ones((1,1))
                else:
                    weights = np.random.uniform(-0.01, 0.01, self.layers[i-1].shape)
                
                self.layers[i].units.append(Neuron(layer.activation_function, weights, 1))
    

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




