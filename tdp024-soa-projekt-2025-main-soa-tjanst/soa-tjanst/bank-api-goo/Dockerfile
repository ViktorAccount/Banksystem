FROM golang:1.25-alpine AS builder
WORKDIR /app

COPY go.mod go.sum ./
RUN go mod download

COPY . .

RUN CGO_ENABLED=0 GOOS=linux go build -o /app-binary .

FROM alpine:latest
WORKDIR /app

COPY --from=builder /app-binary /app/app-binary

EXPOSE 8070

ENTRYPOINT ["/app/app-binary"]
