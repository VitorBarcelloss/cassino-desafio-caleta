# Carteira digital de cassino

### Tecnologias utilizadas

- Java;
- Spring Framework;
- MySQL;
- JPA;
- JUnit;
- Docker;

### Introdução

Este sistema foi criado para o desafio da Caleta, no qual  dever ser simulado a carteira digital de um cassino, implementando a logica para a realização das transações e persistência dos dados. Foi utilizado o modelo API RestFULL, retornando as respostas em JSON.

### Estrutura do projeto

- Controller: PlayerController, TransactionController;
- Error: ErrorMessage, RestExceptionHandler;
  - Exception: ResourceBadRequestException, ResourceNotFoundException;
- Model: 
  - Entity: Player, Transaction;
  - Request: BetWinRequest, RollbackRequest;
  - Response: BalanceResponse, BetWinResponse, PlayerResponse, RollbackInvalidResponse, RollbackOkResponse;
- Repository: PlayerRepository, TransactionRepository;
- Service: PlayerService, TransactionService;
- Test: TransactionServiceTest;

### Mappings

##### GET /balance/{playerId}

Este endpoint consulta no banco de dados informações referente a um jogador, retornando seu Id e saldo atual.

##### POST /bet

Este endpoint realiza a transação de aposta, buscando o player no banco de dados, verificando se o valor da aposta é menor que o valor do saldo atual, fazendo o saque do valor apostado. Para então gerar uma transação do tipo "bet" e retornar a resposta da aposta contendo o Id do player, Id da transação e saldo atual.

##### POST /win

Este endpoint realiza a transação de ganho, buscando o player no banco de dados, deposita o valor no saldo do player, gera uma nova transação do tipo "win" e retorna a resposta do ganho contendo o Id do player, Id da transação e saldo atual.

##### POST /rollback

Este endpoint serve para desfazer uma transação, buscando no banco de dados informações sobre o player e a transação que deve ser cancelada, passando por verificações de: tipo - se o tipo for "win" deve retornar "code: Invalid", se for "bet" continua; status de cancelamento - se estiver cancelado retorna "code: OK" e o saldo atual do player sem acréscimos; valores - verifica se o valor requerido é o mesmo informado na transação a ser cancelada, se não for retorna um ResourceBadRequestException. Após as verificações, se estiver tudo correto, cancela a transação e atualiza o valor do saldo.

### Testes

##### TestBalance 

Verifica um cenário ideal em que são passadas informações corretas para o método 'balance'.

##### TestBetFirstScenario

Verifica um cenário ideal em que são passadas informações corretas para o método 'bet'.

##### TestBetSecondScenario

Verifica um cenário em que deve retornar uma exceção, passando o usuario correto mas o valor de aposta maior que o valor do saldo atual.

##### TestWin

Verifica um cenário ideal em que são passadas informações corretas para o método 'win'.

##### TestRollbackFirstScenario

Verifica um cenário ideal em que são passadas informações corretas para o método 'rollback'.

##### TestRollbackSecondScenario

Verifica um cenário em que deve retornar "code: Invalid" se o tipo for "win".

##### TestRollbackThirdScenario

Verifica um cenário em que deve retornar "code: OK" e o saldo atual do player sem acréscimos, se a transação já tiver sido cancelada.

##### TestRollbackFourthScenario

Verifica um cenário em que deve retornar um ResourceBadRequestException caso o valor requerido seja diferente do valor da transação.