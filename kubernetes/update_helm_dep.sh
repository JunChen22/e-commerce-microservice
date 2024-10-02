#!/bin/bash
# First, we update the dependencies in the components folder:
for f in ./components/core-services/*; do helm dependency update $f; done

for f in ./components/databases/*; do helm dependency update $f; done

for f in ./components/ELK/*; do helm dependency update $f; done

for f in ./components/infrastructure/*; do helm dependency update $f; done

# Next, we update the dependencies in the environments folder:
for f in  ./environments/*; do helm dep up $f; done

# Finally, we verify that the dependencies for the dev-env folder look good:
helm dep ls ./environments/dev-env/

