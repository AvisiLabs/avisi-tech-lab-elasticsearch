app:
	./gradlew :clean :build :bootJar --no-build-cache && docker build --no-cache -t avisilabs/beer-searcher:latest . &&  docker compose up -d --force-recreate
