# Importerar nödvändiga bibliotek för webbtjänsten och Kafka
from fastapi import FastAPI, Request
from kafka import KafkaProducer
import json
import os

# Skapar en FastAPI-applikation (själva webbtjänsten)
app = FastAPI()

# ---------------------------------------------------------
# Kafka setup system ---

KAFKA_ENABLED = True
try:
    producer = KafkaProducer(
        # Hämtar adressen till Kafka-servern från miljövariabeln KAFKA_HOST.
        # Om den inte finns används standardadressen "kafka-service:9092".
        bootstrap_servers=os.getenv("KAFKA_HOST", "kafka-service:9092"),

        # Anger att meddelanden ska serialiseras till JSON innan de skickas till Kafka.
        value_serializer=lambda v: json.dumps(v).encode("utf-8"),
    )
except Exception as e:
    # Om Kafka inte finns tillgängligt loggas ett meddelande, men tjänsten fortsätter ändå att köra.
    print(" Kafka unavailable, running without producer:", e)
    producer = None
    KAFKA_ENABLED = False



persons = {
    "1": "Jakob Pogulis",
    "2": "Xena",
    "3": "Marcus Bendtsen",
    "4": "Zorro",
    "5": "Q",
}

#http://localhost:8060/person/key?key=3
@app.middleware("http")
async def log_request(request: Request, call_next):
    # Skickar förfrågan vidare till nästa steg i  (själva endpointen)
    response = await call_next(request)

    # Om Kafka är aktiverat skickas en loggrad till Kafka-topic "logs.http.requests".
    if KAFKA_ENABLED and producer:
        try:
            producer.send("logs.http.requests", {
                "service": "person-api-python",          # Namn på tjänsten (identifiering)
                "method": request.method,               # HTTP-metoden (GET, POST osv)
                "path": str(request.url.path),          # Sökvägen som anropades
                "status_code": response.status_code,    # HTTP-statuskod (t.ex. 200, 404)
            })
        except Exception as e:
            # Om loggningen misslyckas skrivs ett felmeddelande till konsolen.
            print("Kafka logging error:", e)
    return response




# GET /person/list
# Returnerar hela listan av personer som en JSON-mappning (key -> namn)
@app.get("/person/list")
def list_persons():
    return persons


# GET
# Söker efter en person baserat på namn och returnerar matchande poster.
@app.get("/person/name")
def find_by_name(name: str):
    return {k: v for k, v in persons.items() if v == name}


@app.get("/person/key")
def find_by_key(key: str):
    return persons.get(key)  



if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8060)
