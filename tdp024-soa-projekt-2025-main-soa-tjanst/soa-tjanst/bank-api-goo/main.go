package main

import (
	"fmt"

	"github.com/gin-gonic/gin"
	"github.com/segmentio/kafka-go"
)

// initKafka skapar en Kafka-writer (producent) som används för att logga HTTP-förfrågningar.
func initKafka() *kafka.Writer {
	writer := &kafka.Writer{
		// Adressen till Kafka-servern i systemet
		Addr: kafka.TCP("kafka-service:9092"),
		// Namnet på den topic i Kafka dit loggarna skickas
		Topic: "logs.http.requests",
		// Gör skrivningen asynkron – snabbare, men eventuella fel syns inte direkt
		Async: true,

		BatchSize: 1,
		// Ingen väntetid mellan batchar (0 = omedelbart)
		BatchTimeout: 0,
	}
	return writer
}

// Den tar emot en Kafka-writer som ska användas i loggningen.
func setupServer(kWriter *kafka.Writer) *gin.Engine {
	// Skapar ett nytt standard-Gin-engine (HTTP-server med routing och logging)
	engine := gin.Default()

	// Lägger till middleware som loggar alla HTTP-förfrågningar till Kafka
	engine.Use(KafkaLogger(kWriter))

	// Skapar tjänstelagret (logik) för banker
	bankService := NewBankService()
	// Skapar hanteraren (controller) som använder banktjänsten
	bankHandler := NewBankHandler(bankService)

	// Registrerar REST-endpoints för /bank (list, name, key)
	MapBankRoutes(engine, bankHandler)

	// Returnerar den färdigbyggda servern
	return engine
}

func main() {
	// Initierar Kafka-skrivaren
	kWriter := initKafka()

	// Säkerställer att Kafka-anslutningen stängs korrekt när programmet avslutas
	defer func() {
		if err := kWriter.Close(); err != nil {
			fmt.Println("Error closing Kafka writer:", err)
		}
	}()

	// Bygger Gin-servern och kopplar ihop alla komponenter
	server := setupServer(kWriter)

	// Startar HTTP-servern på port 8070
	// Om något går fel vid start skrivs ett felmeddelande ut i konsolen
	if err := server.Run(":8070"); err != nil {
		fmt.Println("Failed to start server:", err)
	}
}
