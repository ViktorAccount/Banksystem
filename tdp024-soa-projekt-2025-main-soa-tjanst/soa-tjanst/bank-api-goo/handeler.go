package main

import (
	"context"
	"net/http"
	"strconv"
	"strings"
	"time"

	"github.com/gin-gonic/gin"
	"github.com/segmentio/kafka-go"
)

//

//

// KafkaLogger
// HTTP-anrop
func KafkaLogger(writer *kafka.Writer) gin.HandlerFunc {
	return func(c *gin.Context) {
		// Släpper igenom anropet till nästa steg ( endpointen)
		c.Next()

		// Skapar en text med information om anropet
		logMsg := "Method: " + c.Request.Method +
			"\n\tURL: http://" + c.Request.Host + c.Request.URL.RequestURI() +
			"\n\tStatus: " + strconv.Itoa(c.Writer.Status())

		ctx, cancel := context.WithTimeout(context.Background(), 200*time.Millisecond)
		defer cancel()

		// Skickar loggen till Kafka –
		_ = writer.WriteMessages(ctx, kafka.Message{Value: []byte(logMsg)})
	}
}

type BankService struct{}

func NewBankService() *BankService {
	return &BankService{}
}

// vilket ID (Key) banken har
//
//	vilket namn (Name)  har
type bankDTO struct {
	Key  string `json:"key"`
	Name string `json:"name"`
}

// bankRegistry
// Den innehåller en lista med banker

var bankRegistry = []bankDTO{
	{"1", "SWEDBANK"},
	{"2", "IKANOBANKEN"},
	{"3", "JPMORGAN"},
	{"4", "NORDEA"},
	{"5", "CITIBANK"},
	{"6", "HANDELSBANKEN"},
	{"7", "SBAB"},
	{"8", "HSBC"},
	{"9", "NORDNET"},
}

// ListAll används för att hämta hela listan med banker.

func (s *BankService) ListAll(ctx *gin.Context) {
	ctx.IndentedJSON(http.StatusOK, bankRegistry)
}

// FindByName letar efter en bank med ett visst namn (t.ex. ?name=SWEDBANK).

func (s *BankService) FindByName(ctx *gin.Context) {
	name := ctx.Query("name") // hämtar det användaren skrev i URL:en

	// Går igenom listan av banker, en efter en
	for _, b := range bankRegistry {
		// Kollar om namnet matchar
		if strings.EqualFold(b.Name, name) {
			// Om vi hittar rätt bank skickar vi tillbaka den som svar
			ctx.IndentedJSON(http.StatusOK, b)
			return
		}
	}
	// Om vi inte hittar någon match returnerar vi 404 (inte hittad)
	ctx.IndentedJSON(http.StatusNotFound, nil)
}

// FindByKey letar efter en bank via
func (s *BankService) FindByKey(ctx *gin.Context) {
	key := ctx.Query("key") // hämtar värdet efter ?key=

	// Först letar vi om någon bank har exakt samma nyckel (t.ex. "3")
	for _, b := range bankRegistry {
		if b.Key == key {
			ctx.IndentedJSON(http.StatusOK, b)
			return
		}
	}
	// Om inte , skrev bankens namn istället (t.ex. "NORDEA")
	for _, b := range bankRegistry {
		if strings.EqualFold(b.Name, key) {
			ctx.IndentedJSON(http.StatusOK, b)
			return
		}
	}
	// Om vi fortfarande inte hittar något – svara att banken inte finns
	ctx.IndentedJSON(http.StatusNotFound, nil)
}

//
//

type BankHandler struct {
	logic *BankService
}

func NewBankHandler(logic *BankService) *BankHandler {
	return &BankHandler{logic: logic}
}

// HandleList svarar när någon vill se listan på banker.
func (h *BankHandler) HandleList(ctx *gin.Context) {
	h.logic.ListAll(ctx)
}

// HandleFindByName svarar när någon söker på namn (?name=...)

func (h *BankHandler) HandleFindByName(ctx *gin.Context) {
	h.logic.FindByName(ctx)
}

// HandleFindByKey svarar när någon söker via nyckel (?key=...)
func (h *BankHandler) HandleFindByKey(ctx *gin.Context) {
	h.logic.FindByKey(ctx)
}

// // Här kopplar vi ihop webbadresser med funktionerna som ska köras.
func MapBankRoutes(engine *gin.Engine, handler *BankHandler) {
	group := engine.Group("/bank") // Alla vägar börjar med /bank

	// /bank/list → hämtar alla banker
	group.GET("/list", handler.HandleList)
	//  → letar efter en bank via namn
	group.GET("/name", handler.HandleFindByName)
	// letar efter en bank via ID (key)
	group.GET("/key", handler.HandleFindByKey)
}
