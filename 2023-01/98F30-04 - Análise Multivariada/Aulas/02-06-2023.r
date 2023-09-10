df <- iris              # simplificando/padronizando o nome da base de dados
(m <- colMeans(df[-5])) # vetor de medias

(S <- cov(df[-5]))      # matriz de covariancias

eigen(S)                # autovalores (variancias) e autovetores de S

(av <- prcomp(df[-5]))  # via funcao

names(av)

class(av)

av$rotation

# Calculando a primeira componente principal 'na mão'
pc1 <- av$rotation[1,1]*df[,1] +
  av$rotation[2,1]*df[,2] +
  av$rotation[3,1]*df[,3] +
  av$rotation[4,1]*df[,4]

# colando e removendo pc1
df <- cbind(iris, pc1)
rm(pc1)

# dando uma olhada
head(df)

plot(df$Species, df$pc1)

all.equal(var(df$pc1), eigen(S)$values[1])

all.equal(sd(df$pc1), av$sdev[1])

# Simulação
library(e1071)
M <- 10^3
n <- nrow(df)
prop_train <- .7
acc <- matrix(nrow = M, ncol = 2)
colnames(acc) <- c('acc_null', 'acc_pc1')

ini <- Sys.time()
for(i in 1:M){
  # amostras de treino e teste
  set.seed(i); itrain <- sort(sample(1:n, floor(prop_train*n)))
  itest <- setdiff(1:n, itrain)
  
  # classificador SVM com todas as variaveis
  fit0 <- e1071::svm(Species ~ Sepal.Length + Sepal.Width + Petal.Length + Petal.Width, 
                     data = df[itrain,])
  
  # cl. SVM somente com pc1
  fit1 <- svm(Species ~ pc1, 
              data = df[itrain,])
  
  # predicao
  pre0 <- predict(fit0, df[itest,])
  tab <- table(pre0, df[itest, 'Species'])
  acc[i,1] <- sum(diag(tab)/sum(tab))
  
  pre1 <- predict(fit1, df[itest,])
  tab <- table(pre1, df[itest, 'Species'])
  acc[i,2] <- sum(diag(tab)/sum(tab))
  
  # if(i%%.1*M == 0){
  #   print(paste0(i/M*100, ' %'))
  # }
}
Sys.time()-ini



head(acc)

apply(acc, 2, summary)






#---------------------------------Exercicio 8.1

library(ggfortify)
x <- read.table('https://archive.ics.uci.edu/ml/machine-learning-databases/breast-cancer-wisconsin/wdbc.data', sep = ',')
x2 <- x[,-c(1,2)]

head(x2)

autoplot(prcomp(x2, scale = T), data = x, colour = 'V2', loadings = T, loadings.label = T)




