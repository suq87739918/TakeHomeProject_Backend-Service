package com.TakeHomeProject.Exam.Service;

import com.TakeHomeProject.Exam.Repository.DfwDemoRepository;

import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.locationtech.jts.geom.Point;

import java.util.ArrayList;
import java.util.List;

@Service
public class DfwDemoService {
    @Autowired
    private DfwDemoRepository repository;

    public Integer getPopulationWithinRadius(double lng, double lat, double radius) throws Exception {
        // 使用 WKTReader 创建一个基于 lng 和 lat 的 Point 对象
        WKTReader reader = new WKTReader();
        Point point;
        try {
            point = (Point) reader.read("POINT (" + lng + " " + lat + ")");
        } catch (ParseException e) {
            throw new Exception("Error parsing the given lng and lat into a Point geometry.", e);
        }

        double x = point.getX();
        double y = point.getY();

        return repository.findPopulationWithinRadius(x, y, radius);
    }

    public List<String> getGeoJSONPolygons() {
        List<String> geojsons = repository.findGeoJSONPolygons();
        for (String geojson : geojsons) {
            System.out.println(geojson);
        }
        return geojsons;
    }
    public List<String> getPolygonCentroids() {
        return repository.findPolygonCentroids();
    }

    public List<InterpolationData> getPolygonInterpolations() {
        double totalArea = repository.findTotalArea();
        int totalPopulation = repository.findTotalPopulation();
        List<Object[]> polygonData = repository.findPolygonDataWithInterpolation();

        List<InterpolationData> interpolations = new ArrayList<>();
        for (Object[] record : polygonData) {
            String geoJSON = (String) record[0];
            int population = (int) record[1];
            double area = ((Number) record[2]).doubleValue();
            String centroidGeoJSON = (String) record[3];

            double interpolation = ((area / totalArea) * totalPopulation)/10;

            InterpolationData data = new InterpolationData();
            data.setGeoJSON(geoJSON);
            data.setCentroid(centroidGeoJSON);
            data.setInterpolation(interpolation);

            interpolations.add(data);
        }

        return interpolations;
    }

}
