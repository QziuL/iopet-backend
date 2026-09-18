# 🐾 IoPet — Sistema de Monitoramento e Rastreamento Inteligente de Pets

O **IoPet** é uma plataforma integrada de Internet das Coisas (IoT) e inteligência geoespacial desenvolvida para garantir a segurança, localização em tempo real e bem-estar de animais de estimação. Unindo dispositivos vestíveis (coleiras inteligentes com protótipo ESP32-C3), mensageria assíncrona escalável e processamento espacial avançado, o IoPet permite que tutores acompanhem a trajetória de seus pets e recebam alertas automáticos de fuga ou emergência.

-----

## 🎯 Objetivos do Sistema

  - **Rastreamento Continuo e Não Bloqueante:** Capturar e processar coordenadas geográficas transmitidas via hardware de rastreamento sem comprometer o desempenho da aplicação.
  - **Cerca Virtual Automática (Geofencing):** Permitir que tutores delimitem áreas seguras personalizadas no mapa e detectem instantaneamente quando o pet ultrapassa esses limites.
  - **Gestão Proativa de Alertas:** Notificar o tutor em tempo real via *push notifications* sobre fugas e níveis críticos de bateria do dispositivo.
  - **Histórico de Trajetória e Telemetria:** Registrar de forma auditável e persistente toda a movimentação espacial e o estado de integridade do hardware.

-----

## 💡 Principais Funcionalidades

### 👤 Gestão de Tutores e Pets

  - Cadastro e autenticação segura de tutores.
  - Gerenciamento de múltiplos pets associados a uma única conta de tutor.
  - Perfis detalhados do animal com dados cadastrais, raça, idade e foto.

### 📡 Vinculação e Telemetria de Hardware

  - Associação individualizada entre o pet e o dispositivo rastreador utilizando o endereço físico imutável (MAC Address).
  - Monitoramento contínuo de métricas de saúde do hardware (nível percentual de bateria, status ativo/inativo e horário do último *ping*).
  - Alerta automático de bateria baixa quando o nível atinge 20% ou menos.

### 🗺️ Cerca Virtual e Geofencing Espacial

  - Configuração intuitiva de polígonos geométricos de segurança no mapa para cada pet.
  - Algoritmo de cruzamento espacial de dados em tempo real, validando se a posição GPS atual (*Point*) intercepta o perímetro cadastrado (*Polygon*).
  - Geração automática de eventos de alerta de fuga com timestamp e posição exata.

### 📊 Histórico e Notificações

  - Registro cronológico das rotas e movimentação do pet ao longo do tempo.
  - Central de notificações auditável, registrando o histórico de alertas pendentes e visualizados pelo tutor.

-----

## 🏗️ Visão Geral da Arquitetura de Dados

O ecossistema **IoPet** opera sob o modelo de mensageria assíncrona baseada em eventos para suportar altas cargas de pacotes IoT simultâneos:

1.  **Coleta de Dados (IoT):** O dispositivo vestível (ESP32-C3) obtém o posicionamento global e transmite os pacotes de dados.
2.  **Centralização e Roteamento (Message Broker):** O RabbitMQ atua como o intermediário centralizador, armazenando as mensagens em filas persistentes e garantindo resiliência contra perdas de sinal.
3.  **Processamento de Negócio (Backend API):** A API Spring Boot consome as mensagens da fila de forma não bloqueante, atualiza a integridade do dispositivo e invoca o motor geoespacial.
4.  **Armazenamento Espacial (Database):** O banco PostgreSQL com a extensão PostGIS persiste as coordenadas geométricas com precisão e calcula as interseções de geofencing.
5.  **Notificação ao Usuário (Push Notifications):** Em caso de violação de perímetro, o serviço de notificações dispara um alerta imediato para o aplicativo móvel do tutor.

-----

## 🌐 Domínio do Sistema (Principais Entidades)

  - **Tutor:** Entidade responsável pelo gerenciamento da conta e controle de acessos.
  - **Pet:** Perfil do animal de estimação contendo as configurações de sua cerca virtual espacial.
  - **Dispositivo IoT:** Representação do hardware de rastreamento com identificador único (MAC Address).
  - **Histórico de Localização:** Registro de séries temporais das coordenadas geográficas enviadas.
  - **Alerta de Geofencing:** Registro auditável dos disparos de emergência decorrentes de fugas ou alertas do sistema.

-----

## 🔒 Segurança e Privacidade

  - **Controle de Acesso Flexível:** Cada tutor possui visibilidade e gerenciamento restritos exclusivamente aos seus próprios pets e dispositivos associados.
  - **Proteção de Credenciais:** As senhas dos usuários são protegidas com criptografia forte antes da persistência.
  - **Exposição Segura de Recursos:** A API REST utiliza identificadores públicos únicos (UUID) nas rotas externas, preservando a estrutura de índices numéricos internos do banco de dados.

-----

### Projeto desenvolvido por Luiz Fernando Quinholi Mendes.
