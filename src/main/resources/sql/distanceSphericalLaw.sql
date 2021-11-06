use cheapset;


DELIMITER $$
DROP FUNCTION IF EXISTS distanceSphericalLaw$$
 
CREATE FUNCTION distanceSphericalLaw(
        lat1 DOUBLE, lon1 DOUBLE,
        lat2 DOUBLE, lon2 DOUBLE
     ) RETURNS Double
    NO SQL DETERMINISTIC
    COMMENT 'Returns the distance in degrees on the Earth
             between two known points of latitude and longitude'
BEGIN
    RETURN ACOS(
              SIN(RADIANS(lat1)) *
              SIN(RADIANS(lat2)) +
              COS(RADIANS(lat1)) * 
              COS(RADIANS(lat2)) *
              COS(RADIANS(lon2) - RADIANS(lon1))
            ) * 6378.1;
END$$
 
DELIMITER;