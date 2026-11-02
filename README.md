# DocReader
AI-powered Document Q&amp;A using RAG, Spring Boot, React, and Generative AI.

🤖 GenAI Document Question Answering System (RAG)
An AI-powered Document Question Answering application that allows users to upload documents and ask questions in natural language. The system uses Retrieval-Augmented Generation (RAG) to retrieve relevant information from uploaded documents and generate accurate, context-aware answers using Large Language Models (LLMs).

🚀 Features
User Authentication: Secure registration and login using Spring Security and JWT.
Document Upload: Upload and manage PDF and other supported documents.
Document Management: View, retrieve, and delete uploaded documents.
AI-Powered Question Answering: Ask natural-language questions about uploaded documents.
RAG Architecture: Retrieve relevant document chunks to generate context-aware answers.
Semantic Search: Find relevant document content using vector embeddings and similarity search.
AI Chat: Interact with uploaded documents through a conversational interface.
Real-Time Streaming: Stream AI-generated responses to the frontend.
Role-Based Access Control: Secure document and administrative operations.
🛠️ Technology Stack
Frontend
React.js
TypeScript
Axios
Zustand
HTML5
CSS3
Backend
Java
Spring Boot 3
Spring Security
Spring Data JPA
JWT Authentication
REST APIs
Generative AI
Retrieval-Augmented Generation (RAG)
Large Language Models (LLMs)
Text Embeddings
Semantic Similarity Search
Prompt Engineering
Database and Storage
PostgreSQL
Vector Database / Vector Search
🏗️ System Architecture
The application follows a Retrieval-Augmented Generation (RAG) architecture.


text
                 USER
                   |
                   v
             REACT FRONTEND
                   |
                   v
           SPRING BOOT BACKEND
                   |
          +--------+--------+
          |                 |
          v                 v
    DOCUMENT UPLOAD     USER QUESTION
          |                 |
          v                 v
    TEXT EXTRACTION    QUESTION EMBEDDING
          |
          v
    TEXT CHUNKING
          |
          v
    GENERATE EMBEDDINGS
          |
          v
    VECTOR DATABASE
          |
          +--------+--------+
                   |
                   v
           SIMILARITY SEARCH
                   |
                   v
          RETRIEVE RELEVANT
           DOCUMENT CHUNKS
                   |
                   v
             LLM PROCESSING
                   |
                   v
          CONTEXTUAL ANSWER
                   |
                   v
             REACT FRONTEND
⚙️ Application Workflow
1. User Authentication
Users register and log in.
JWT tokens are generated after successful authentication.
Protected API endpoints validate JWT tokens.
2. Document Upload
Users upload documents through the React frontend.
Spring Boot receives and processes uploaded files.
Text is extracted from supported document formats.
Extracted text is divided into smaller chunks.
3. Embedding Generation
Each document chunk is converted into a numerical vector using an embedding model.
Embeddings are stored in the vector database with document metadata.
4. Question Processing
Users submit questions through the chat interface.
The question is converted into an embedding.
Semantic similarity search retrieves relevant document chunks.
5. Answer Generation
Retrieved document content is provided to the LLM as context.
The LLM generates a context-aware answer.
The generated response is returned to the frontend.
6. Streaming Responses
AI responses can be delivered incrementally using Server-Sent Events (SSE).
Users can see responses as they are generated.
