package sevenstar.marineleisure.global;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.feature.FeatureIterator;
import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geom.PrecisionModel;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.referencing.operation.MathTransform;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sevenstar.marineleisure.global.enums.Region;
import sevenstar.marineleisure.spot.domain.RegionBoundary;
import sevenstar.marineleisure.spot.domain.RegionBoundaryRepository;

@Component
@RequiredArgsConstructor
@Slf4j
public class RegionBoundaryShpImporter implements CommandLineRunner {

	private final RegionBoundaryRepository regionBoundaryRepository;

	@Override
	@Transactional
	public void run(String... args) throws Exception {
		if (regionBoundaryRepository.count() > 0) {
			return;
		}

		// 좌표계 transform (Korea Central Belt 2010, EPSG:5178) => (WGS84, 위경도)
		MathTransform transform = CRS.findMathTransform(
			CRS.decode("EPSG:5186", true),
			CRS.decode("EPSG:4326", true),
			true);

		File baseDir = new File("src/main/resources/land");
		File[] subDirs = baseDir.listFiles(File::isDirectory);
		if (subDirs == null) {
			log.warn("No subdirectories found.");
			return;
		}

		for (File dir : subDirs) {
			File shpFile = findShpFile(dir);
			if (shpFile == null) {
				log.warn("No .shp file in: {}", dir.getName());
				continue;
			}

			String[] fileNameParts = dir.getName().split("_");
			Region region = Region.fromAddress(fileNameParts[fileNameParts.length - 1]);
			List<Polygon> polygons = new ArrayList<>();

			FileDataStore store = FileDataStoreFinder.getDataStore(shpFile);
			var featureSource = store.getFeatureSource();
			try (FeatureIterator<SimpleFeature> features = featureSource.getFeatures().features()) {
				while (features.hasNext()) {
					SimpleFeature feature = features.next();
					Object geometryObj = feature.getDefaultGeometry();

					if (geometryObj instanceof Geometry originalGeometry) {
						// 좌표계 변환
						Geometry transformed = JTS.transform(originalGeometry, transform);

						if (transformed instanceof Polygon polygon) {
							polygons.add(polygon);
						} else if (transformed instanceof MultiPolygon multiPolygon) {
							for (int i = 0; i < multiPolygon.getNumGeometries(); i++) {
								polygons.add((Polygon)multiPolygon.getGeometryN(i));
							}
						}
					}
				}
			}

			// 변환된 polygon들 저장
			if (!polygons.isEmpty()) {
				GeometryFactory factory = new GeometryFactory(new PrecisionModel(), 4326);
				MultiPolygon merged = factory.createMultiPolygon(polygons.toArray(new Polygon[0]));
				RegionBoundary boundary = new RegionBoundary(region, merged);
				regionBoundaryRepository.save(boundary);
			}

			store.dispose();
		}
	}

	private File findShpFile(File dir) {
		File[] files = dir.listFiles((d, name) -> name.toLowerCase().endsWith(".shp"));
		return (files != null && files.length > 0) ? files[0] : null;
	}

}
