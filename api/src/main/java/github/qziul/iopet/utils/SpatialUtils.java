package github.qziul.iopet.utils;

import github.qziul.iopet.controller.dto.request.GeoPointDTO;
import org.locationtech.jts.geom.*;

import java.util.ArrayList;
import java.util.List;

public class SpatialUtils {
    private static final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

    public static Point criarPonto(double latitude, double longitude) {
        // ordem dos parâmetros Longitude/X, Latitude/Y
        return geometryFactory.createPoint(new Coordinate(longitude, latitude));
    }

    public static Polygon criarPoligono(List<GeoPointDTO> pontos) {
        if (pontos == null || pontos.size() < 3) {
            throw new IllegalArgumentException("Um polígono de geofencing necessita de ao menos 3 pontos.");
        }

        List<Coordinate> coords = new ArrayList<>();
        for (GeoPointDTO p : pontos) {
            coords.add(new Coordinate(p.longitude(), p.latitude()));
        }

        // Garante fechamento do polígono conforme especificação JTS (primeiro ponto igual ao último)
        GeoPointDTO primeiro = pontos.get(0);
        GeoPointDTO ultimo = pontos.get(pontos.size() - 1);
        if (!primeiro.latitude().equals(ultimo.latitude()) || !primeiro.longitude().equals(ultimo.longitude())) {
            coords.add(new Coordinate(primeiro.longitude(), primeiro.latitude()));
        }

        LinearRing shell = geometryFactory.createLinearRing(coords.toArray(new Coordinate[0]));
        return geometryFactory.createPolygon(shell);
    }

    public static List<GeoPointDTO> extrairPontos(Geometry geometry) {
        if (geometry == null) return List.of();
        Coordinate[] coordinates = geometry.getCoordinates();
        List<GeoPointDTO> pontos = new ArrayList<>();
        int len = coordinates.length;
        // Se o último ponto for o fechamento duplicado, removemos para conveniência do frontend
        if (len > 1 && coordinates[0].equals2D(coordinates[len - 1])) {
            len--;
        }
        for (int i = 0; i < len; i++) {
            pontos.add(new GeoPointDTO(coordinates[i].getY(), coordinates[i].getX()));
        }
        return pontos;
    }
}
