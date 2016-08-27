package org.geotools.polylabel;

import java.util.List;

import org.geotools.filter.capability.FunctionNameImpl;
import org.geotools.util.Converters;
import org.opengis.filter.capability.FunctionName;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.expression.ExpressionVisitor;
import org.opengis.filter.expression.Function;
import org.opengis.filter.expression.Literal;

import com.vividsolutions.jts.awt.PointShapeFactory.Point;
import com.vividsolutions.jts.geom.Geometry;

public class PolygonLabelFunction implements Function {
	static FunctionName NAME = new FunctionNameImpl("labelPoint", Point.class,
			FunctionNameImpl.parameter("polygon",  Geometry.class),
			FunctionNameImpl.parameter("tolerance", double.class));
	
	private final List<Expression> parameters;
    
    private final Literal fallback;
    
    public PolygonLabelFunction(List<Expression> parameters, Literal fallback) {
        if (parameters == null) {
            throw new NullPointerException("parameters required");
        }
        if (parameters.size() != 2) {
            throw new IllegalArgumentException(
                    "labelPoint((multi)polygon, tolerance) requires two parameters only");
        }
        this.parameters = parameters;
        this.fallback = fallback;
    }
    
    public Object evaluate(Object object) {
        return evaluate(object, Point.class);
    }
    
    public <T> T evaluate(Object object, Class<T> context) {
        Expression geometryExpression = parameters.get(0);
        Geometry polygon = geometryExpression.evaluate(object, Geometry.class);
    
        Expression toleranceExpression = parameters.get(1);
        double tolerance = toleranceExpression.evaluate(object, double.class);
    
        Geometry point = PolyLabeller.getPolylabel(polygon, tolerance);
        
        return Converters.convert(point, context); // convert to requested format
    }
    
    public Object accept(ExpressionVisitor visitor, Object extraData) {
        return visitor.visit(this, extraData);
    }
    
    public String getName() {
        return NAME.getName();
    }
    
    public FunctionName getFunctionName() {
        return NAME;
    }
    
    public List<Expression> getParameters() {
        return parameters;
    }
    
    public Literal getFallbackValue() {
        return fallback;
    }
}
