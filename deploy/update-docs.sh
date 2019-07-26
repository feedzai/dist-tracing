#!/bin/bash

if [[ "$TRAVIS_PULL_REQUEST" = "false" && $(git diff github -- ../site/ | wc -l) -gt 0 ]];
then
	cd ../site/src/gitbook
	gitbook install
	gitbook build
	cd -
	cp -R ../site/src/gitbook/_book/* ../docs/
	git add ../docs/
	git commit -m "[TravisCI] Update Documentation"
	git push github master
fi
