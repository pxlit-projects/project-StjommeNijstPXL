# Architecture

![image](https://github.com/user-attachments/assets/acbeae58-bdb7-45f4-9289-235264fc5cbb)


# Post Service: 
De redactuer kan posts aanmaken, updaten en een post kunnen opslaan als concept. De gebruiker kan de posts zien en filteren.

# Review Service: 
De redacteur wil posts kunne bekijken en goedkeuren, meldingen ontvangen van de goed of afkeuring en opmerkingen kunnen toevoegen bij een afwijzing.

# Comment Service:
De gebruiker kan een reactie plaatsen op een post, reacties van andere collega's kunnen lezen en eigen reacties kunnen bewerken.

# Synchrone Communicatie: 
Hierbij wordt gebruikt gemaakt van openfeign.

# Asynchrone Communicatie: 
Hierbij wordt gebruikt gemaakt van RabbitMq.

# API Gateway: 
Wordt gebruikt als centraal toegangspunt voor alle client-verzoeken.

# Eureka: 
Hier wordt een aparte DiscoveryService voor gebruikt zodat alle microservices zich hierbij kunnen aanmelden en elkaar gemakkelijker kunnen vinden voor communicatie.

# Config Service: 
Een algemene microservice waarbij alle configuratie (application.properties) worden bijgehouden van alle microservices.


# Communicatietypes per User Story

| **User Story**                           | **Communicatietype**        | **Opmerking**                                    |
|------------------------------------------|-----------------------------|--------------------------------------------------|
| **US1**: Nieuwe posts aanmaken           | Synchroon (OpenFeign)       | Communicatie binnen de PostService               |
| **US2**: Opslaan als concept             | Synchroon (OpenFeign)       | Communicatie binnen de PostService               |
| **US3**: Posts bewerken                  | Synchroon (OpenFeign)       | Communicatie binnen de PostService               |
| **US4**: Overzicht gepubliceerde posts   | Synchroon (OpenFeign)       | Communicatie met PostService                     |
| **US5**: Filteren van posts              | Synchroon (OpenFeign)       | Communicatie met PostService                     |
| **US7**: Posts goedkeuren/afwijzen       | Synchroon (OpenFeign)       | Communicatie tussen PostService en ReviewService |
| **US8**: Melding bij goedkeuren/afwijzen | Asynchroon (RabbitMQ)       | Communicatie tussen ReviewService en PostService |
| **US9**: Opmerkingen bij afwijzing       | Synchroon (OpenFeign)       | Communicatie binnen ReviewService                |
| **US10**: Reacties plaatsen              | Synchroon (OpenFeign)       | Communicatie met CommentService                  |
| **US11**: Reacties lezen                 | Synchroon (OpenFeign)       | Communicatie met CommentService                  |
| **US12**: Reacties bewerken/verwijderen  | Synchroon (OpenFeign)       | Communicatie met CommentService                  |


