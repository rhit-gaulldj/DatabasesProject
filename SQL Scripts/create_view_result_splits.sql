CREATE VIEW result_splits AS
SELECT [ResultSplit].result_id, STRING_AGG(Concat(time, '/', distance,distance_unit),' | ') as splits
FROM [ResultSplit]
GROUP BY result_id;

	