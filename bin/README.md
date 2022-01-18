Desafio BECCA:

Demanda: Construir dois servicos que serao responsaveis por deteminadas tarefas na conta bancaria dos cliente de um banco
* Cadastrar novos clientes na base de dados do banco
* Realizar todas as operacoes basicas de um banco (saldo, extrato, deposito, saque e transferencia)
* Cada cliente tem um numero gratuito de saques mensal, e apos isso, e cobrada uma taxa por cada saque. 
    O numero gratuito de saques e informado no momento do cadastro da conta do cliente, de acordo com o tipo da conta
    - Conta tipo pessoa fisica, tera 5 saques gratuitos por mes, apos isso deve ser cobrado R$10 por cada saque
    - Conta tipo pessoa juridica tera 50 saques gratuitos por mes, apos isso deve ser cobrado R$10 por cada saque
    - Conta governamental tera 250 saques gratuitos por mes, apos isso deve ser cobrado R$20 por cada saque
    - O valor adicional do saque deve ser descrescido do valor do saldo do cliente (Saque de 100 reais, ao ser cobrado 5 reais de taxa, o cliente deve ter no minimo 105 reais de saldo)

Funcionalidades do servico 1 (responsavel pelo controle da conta bancaria do usuario): 
    1 - Efetuar o cadastro de novos clientes
        * um cliente pode ter mais de uma conta
        * cadastrar dados basicos do cliente (nome, cpf, telefone e endereco)
    2 - Efetuar cadastro da conta
        * agencia
        * numero da conta
        * tipo da conta 
        * digito verificador
        * cliente (cpf)
    2 - Efetuar operacoes de consulta de saldo e extrato, realizar saque e transferencia 
    3 - Receber a entrada de informacoes para cadastrar a quantidade de saques gratuitos por mes. (operacao assincrona)
    4 - Apenas saques, verificar no cache a quantidade de saques gratis, caso tenha disponibilidade, nao cobrar taxa e notificar (mensageria) o uso ao servico responsavel

Funcionalidades do servico 2 (responsavel pelo controle da quantidade de saques gratuitos):
    1 - criar o limite de saque gratuito por mes para o cliente
    2 - controlar o uso, tanto na base de dados como no cache.

Requisitos tecnicos na construcao dos servicos:
- Implementar arquitetura de microservicos
- Utilizar base de dados SQL para armazenar os dados
- Utilizar REDIS para implementar o cache
- Utilizar KAFKA para servicos de mensageria
- Utilizar java 11 
- Utilizar boas praticas de programacao (clean code)
