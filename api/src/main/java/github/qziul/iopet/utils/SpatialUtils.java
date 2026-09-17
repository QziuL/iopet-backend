package github.qziul.iopet.utils;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;

public class SpatialUtils {
    private static final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

    public static Point criarPonto(double latitude, double longitude) {
        // ordem dos parâmetros Longitude/X, Latitude/Y
        return geometryFactory.createPoint(new Coordinate(longitude, latitude));
    }
}
