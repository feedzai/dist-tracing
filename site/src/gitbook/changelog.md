# Changelog

{% for commit in book.changelog %}{% if commit.tags | length == 0 and loop.first %}

ID  | Author | Date | Summary
--- | ------ | ---- | -------
[{{commit.abbrId}}]({{book.maven['scm.url']}}/commit/{{commit.id}})|{{commit.authorName}}|{{commit.dateAsIso8601}} {{commit.timeAsIso8601}}|{{commit.message}}
{% elif commit.tags | length > 0 %}

ID  | Author | Date | Summary
--- | ------ | ---- | -------
[{{commit.abbrId}}]({{book.maven['scm.url']}}/commit/{{commit.id}})|{{commit.authorName}}|{{commit.dateAsIso8601}} {{commit.timeAsIso8601}}|{{commit.message}}
{% else %}[{{commit.abbrId}}]({{book.maven['scm.url']}}/commit/{{commit.id}})|{{commit.authorName}}|{{commit.dateAsIso8601}} {{commit.timeAsIso8601}}|{{commit.message}}
{% endif %}{% endfor %}
