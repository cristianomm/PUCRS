library(mctest)
library(factoextra)
install.packages("ICSNP")

#Q1
#Q1. (3.0) Foi observada uma amostra de clientes, que opinaram com notas de 0 a 10 sobre um serviço prestado. 
#  Considere X1: pontualidade, X2: conhecimento e X3: disponibilidade.
  
#a. (0.5) Defina n e p a partir das informações do BLOCO 1A.

#b. (0.5) Indique o que são as medidas intituladas Medida 1, Medida 2 e Medida 3, calculadas no BLOCO 1B.

#c. (0.5) Apresente o passo-a-passo de como é calculado o valor -0.4040610 da matriz R e interprete-o.

#d. (0.5) No BLOCO 1C indique os testes que estão sendo realizados, suas hipóteses H0 e H1 e qual a sua
#  decisão em cada um deles considerando α = 5%.

#e. (0.5) No BLOCO 1D indique qual teste está sendo realizado, quais as hipóteses H0 e H1 e qual sua decisão
# considerando α = 5%.

#f. (0.5) Quais diferenças você identifica entre os testes realizados nos blocos 1C e 1D?

  
# BLOCO 1A
(X <- read.table('https://filipezabala.com/data/clientes.txt', header = T, sep = '\t'))
  
# BLOCO 1B
(m <- colMeans(X)) # Medida 1

(S <- cov(X)) # Medida 2

(R <- cor(X)) # Medida 3


# BLOCO 1C
t.test(X$X1, mu = 8)

t.test(X$X2, mu = 8)

t.test(X$X3, mu = 8)

# BLOCO 1D
ICSNP::HotellingsT2(X, mu = c(8,8,8))

  
#####################################################################################################################







#Q2. (2.5) (He, Zhang, and Zhang 2016) utilizaram a fluorescência de raios-X (ED-XRF) para determinar a composição química 
#de cerâmicas antigas, detalhadas no banco de dados Composição Química de Amostras Cerâmicas1.

#a. (1.0) Considerando os métodos ‘wss’ (within sums of squares) e ‘silhouette’ do BLOCO 2B, determine k, o número ótimo de clusters.

#b. (1.0) Utilizando o resultado do item anterior, circule no BLOCO 2C com lápis, caneta ou virtualmente os k grupos determinados no item anterior.

#c. (1.0) Explique o que ocorre no comando prcomp(dat[,-(1:2)], scale = TRUE) do BLOCO 2E.

#d. (1.0) Considerando o gráfico das duas primeira componentes principais do BLOCO 2F, você considera que estas
#duas dimensões são úteis para diferenciar as partes da cerâmica (Body/Glaze ou Corpo/Verniz)? 
#Que pontos de corte você sugere em cada dimesão dos gráficos?
  

# BLOCO 2A
dat <- read.csv('https://filipezabala.com/data/ceramic.csv', head = T)
dplyr::glimpse(dat)


# BLOCO 2B
dat_sc <- scale(dat[,-(1:2)])
p1 <- factoextra::fviz_nbclust(dat_sc, kmeans, method = 'wss')
p2 <- factoextra::fviz_nbclust(dat_sc, kmeans, method = 'silhouette')
gridExtra::grid.arrange(p1, p2, ncol = 2, heights = grid::unit(7.5, c('cm')))


# BLOCO 2C
hc <- hclust(dat_sc)
fviz_dend(hc, k = 2,  # 2 grupos
          cex = 0.6,  # tamanho do texto/rótulo (label)
          rect = TRUE # adiciona retângulos ao redor dos grupos
)

# BLOCO 2D
p1 <- factoextra::fviz_nbclust(dat_sc, kmeans, method = 'wss')
p2 <- factoextra::fviz_nbclust(dat_sc, kmeans, method = 'silhouette')
gridExtra::grid.arrange(p1, p2, ncol = 2, heights = grid::unit(7.5, c('cm')))


# BLOCO 2E
cp <- prcomp(dat[,-(1:2)])
cp_sc <- prcomp(dat[,-(1:2)], scale = TRUE)


# BLOCO 2F
library(ggfortify)
a1 <- autoplot(cp, data = dat, colour = 'Part', main = 'Original', loadings = T, loadings.label = T, type = 'raw')
a2 <- autoplot(cp_sc, data = dat, colour = 'Part', main = 'Padronizado', loadings = T, loadings.label = T, type = 'raw')
gridExtra::grid.arrange(a1, a2, ncol = 2, heights = grid::unit(7.5, c('cm')))




#####################################################################################################################

#Q3. (2.5) Ainda com os dados da Questão 2, responda os itens a seguir considerando as informações dos Blocos.
#a. (0.5) Detalhe o que ocorre no Bloco 3A.
#b. (0.5) O que você diria a respeito do modelo fit0 apresentado no Bloco 3B?
#c. (0.5) O que você diria a respeito da predição do Bloco 3C?
#d. (0.5) Detalhe o que ocorre no Bloco 3D.
#e. (0.5) O que você diria a respeito da predição do Bloco 3E? Compare com a predição do item c.

# BLOCO 3A
dat$y <- 1
dat$y[dat$Part == 'Glaze'] <- 0
dat$y <- (dat$y)
table(dat$Part, dat$y)

dplyr::glimpse(dat)

set.seed(4); itrain <- sort(sample(1:nrow(dat), floor(.6*nrow(dat))))
train <- dat[itrain,-(1:2)]
test <- dat[-itrain,-(1:2)]

# BLOCO 3B
fit0 <- glm(y ~ ., data = train, family = 'binomial')

summary(fit0)

mctest::mctest(fit0)

sort(car::vif(fit0), decreasing = TRUE)


# BLOCO 3C
pred <- round(predict(fit0, test, type = 'response'))
(cm <- table(test$y, pred))

# BLOCO 3D
pc <- prcomp(dat[,-c(1:2,20)], scale = TRUE)
train_pc <- cbind(train['y'], pc1 = pc$x[itrain,1])
train_pc <- cbind(train_pc, pc2 = pc$x[itrain,2])
test_pc <- cbind(test['y'], pc1 = pc$x[-itrain,1])
test_pc <- cbind(test_pc, pc2 = cp$x[-itrain,2])
fit1 <- glm(y ~ ., data = train_pc, family = 'binomial')

# BLOCO 3E
pred <- round(predict(fit1, test_pc, type = 'response'))
(cm <- table(test_pc$y, pred))







#####################################################################################################################

#Q4
# BLOCO 4A
url <- 'https://archive.ics.uci.edu/ml/machine-learning-databases/ionosphere/ionosphere.data'
dat <- read.csv(url, header = TRUE)
dat$y <- 1
dat$y[dat$g == 'b'] <- 0
dat <- dat[,-c(1:2,35)]
colnames(dat)[-33] <- paste0('V',1:32)

# BLOCO 4B
set.seed(314); itrain <- sort(sample(1:nrow(dat), floor(.7*nrow(dat))))
train <- dat[itrain,]
test <- dat[-itrain,]

# BLOCO 4C
fit0 <- glm(y ~ ., data = train, family = 'binomial')
summary(fit0)

mctest::mctest(fit0)


sort(car::vif(fit0), decreasing = TRUE)

# BLOCO 4D
fit <- step(fit0, trace = 0)
summary(fit)

mctest::mctest(fit)

sort(car::vif(fit), decreasing = TRUE)

# BLOCO 4E
pred <- round(predict(fit0, test, type = 'response'))
(cm <- table(test$y, pred))
#resulta em 89, soma de 29 + 60



