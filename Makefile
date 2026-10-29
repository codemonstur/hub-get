.DEFAULT_GOAL := help
.EXPORT_ALL_VARIABLES:
.PHONY: help clean docs build test native install run validate release-notes coverage deploy mutation

NAME=`xmllint --xpath "/project/artifactId/text()" pom.xml`
VERSION=`xmllint --xpath "//project/version/text()" pom.xml`
PREVIOUS_TAG=`git tag | sort -r | head -n 1`

# Blue [INFO] prefix before every message, NO_COLOR=1 prints it plain
ifneq ($(NO_COLOR),)
INFO = printf '[INFO] %s\n'
else
INFO = printf '[\033[1;34mINFO\033[m] %s\n'
endif

help: ## Show this help
	@grep -hE '^[a-zA-Z0-9_.-]+:' $(MAKEFILE_LIST) | grep -vE '^[a-zA-Z0-9_.-]+:=' | \
		awk 'BEGIN {FS = ":"} /^\./ {next} {name = $$1; sub(/[[:blank:]]+$$/, "", name); doc = $$0; sub(/^[^#]*## /, "", doc); if (doc == $$0) sub(/^[^:]*:[[:blank:]]*/, "", doc); printf "  \033[36m%-20s\033[0m %s\n", name, doc}'

clean: ## Delete everything in ./target
	@$(INFO) "Cleaning"
	@mvn clean

# Install on debian with:
#     apt-get install pandoc
#     apt-get install texlive-latex-base
#     apt-get install weasyprint
docs: ## Generate project documentation and write to target/docs
	@mkdir -p target/docs
	@pandoc --pdf-engine=weasyprint -o target/docs/README.pdf README.md

# Builds the code
build: ## Compile and package the code, skipping tests
	@$(INFO) "Building"
	@mvn -DskipTests clean package

test: ## Run the tests
	@mvn test

coverage: ## Run tests and generate a jacoco coverage report
	@$(INFO) "Running tests and generating coverage report"
	@mvn test
	@$(INFO) "Coverage report written to target/site/jacoco/index.html"
	@output=$$(java src/build/CoverageSummary.java target/site/jacoco/jacoco.csv) || exit $$?; \
	printf '%s\n' "$$output" | while IFS= read -r line; do $(INFO) "$$line"; done

native: build ## Build a native binary with GraalVM
	@native-image --no-fallback -jar target/filter.jar -o target/filter
	@chmod +x target/filter

install: native ## Install the native binary to /usr/local/bin
	@sudo mv target/filter /usr/local/bin

run: build ## Build and run with the sample stream and reorder config
	@$(INFO) "Running"
	@cat src/data/stream.txt | java -jar target/filter.jar -c src/data/reorder.yml

mutation: ## Run mutation tests on the unit tests
	@$(INFO) "Running mutation tests"
	@mvn -P mutation test
	@$(INFO) "Mutation report written to target/pit-reports/index.html"


validate: ## Check dependency versions, cves, licenses, and code quality
	@mvn com.github.codemonstur:maven-check-pom:1.0.0:check validate
	@mvn com.github.codemonstur:maven-check-pom:1.0.0:reorder
	@mvn versions:display-dependency-updates "-Dmaven.version.ignore=(?i).*(alpha|beta|rc|-m[0-9]+|snapshot).*"
	@mvn versions:display-plugin-updates
	@mvn verify -P vulnerabilities,licenses -DskipTests -T 1C
	@mvn validate -P codequality

release-notes: ## Write release notes from the git log
	@mkdir -p src/docs/releases
	@$(INFO) "Writing release notes to src/docs/releases/release-$(VERSION).txt"
	@echo "$(VERSION)" >> src/docs/releases/release-$(VERSION).txt
	@echo "" >> src/docs/releases/release-$(VERSION).txt
	@git log --pretty="%s" $(PREVIOUS_TAG)... master >> src/docs/releases/release-$(VERSION).txt

deploy: ## Upload to Maven Central, create a GitHub release, tag and push
	@$(INFO) "Uploading to maven central"
	@mvn clean deploy
	@$(INFO) "Creating github release"
	@gh release create $(NAME)-$(VERSION) target/$(NAME).jar target/$(NAME)-javadoc.jar target/$(NAME)-sources.jar -F src/docs/releases/release-$(VERSION).txt
	@$(INFO) "Tagging and pushing to github"
	@git tag $(NAME)-$(VERSION)
	@git push && git push --tags
