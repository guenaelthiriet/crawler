# Crawler

This is a very simple web crawler in Java.

# Limitations

URLs with different protocols will be downloaded twice.
Subdomain don't work. This also means that if there is a redirect to another sub or parent domain the target domain will not be saved. For instance www.fiska.com will be redirected to fiska.com and a such processing will stop very quickly

# Build

    ./gradlew build

# Run

    ./gradlew bootRun -Pargs=--app.baseUrl=https://fiska.com