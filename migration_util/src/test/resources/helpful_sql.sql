--regex replace in json 
UPDATE item_version SET metadata = regexp_replace(metadata::text, '"escidoc\:([0-9]+)"', '"ou_\1"', 'g')::jsonb;