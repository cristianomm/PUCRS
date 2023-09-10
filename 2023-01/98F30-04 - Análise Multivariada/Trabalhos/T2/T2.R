# Instalando pacotes
chooseCRANmirror(ind = 11) # Brazil
packs <- c('devtools', 'ellipse', 'klaR', 'rgl', 'VGAM')
new.packages <- packs[!(packs %in% installed.packages()[,"Package"])]
if(length(new.packages)) install.packages(packs, dep=TRUE)
update.packages(checkBuilt = TRUE, ask = FALSE)
devtools::install_github('filipezabala/desempateTecnico', force = T)

# Chamando biblioteca
library(desempateTecnico)

n <- 6754
prop_votos <- c(.47, .33, .07, .05, .01, .04, .02+.01)
candidatos <- c('lula','bolsonaro','ciro','tebet','soraya','branco-nulo','nao-sei')

# Crie um dataframe com os dados
df <- data.frame(candidatos, prop_votos)

# Exclua os votos em branco, nulos e 'não sei'
df_validos <- df[!df$candidatos %in% c('branco-nulo', 'nao-sei'), ]

# Calcule o número total de votos válidos
n_validos <- sum(df_validos$prop_votos * n)

# Calcule as proporções de votos válidos para cada candidato
df_validos$prop_votos_validos <- df_validos$prop_votos * n / n_validos

# Imprima o número total de votos válidos e as proporções de votos válidos
print(n_validos)
print(df_validos)



# Defina as proporções de votos dos três principais candidatos
votos <- c(0.505376344, 0.35483871, 0.075268817)

n_validos <- 6281.22
simplex3d(0.505376344, 0.35483871, 0.075268817, n=n_validos)



df_validos$prop_votos_validos
(df_validos$prop_votos_validos*n_validos) + 1
ci_prop(votos[1]*n_validos, n_validos)
ci_prop(votos[2]*n_validos, n_validos)
ci_prop(votos[3]*n_validos, n_validos)

df_validos$prop_votos_validos
n_validos
bayes(c(0.50537634, 0.35483871, 0.07526882), n_validos)
bayes(c(0.50537634, 0.35483871, 0.07526882, 0.05376344, 0.01075269), n_validos)
