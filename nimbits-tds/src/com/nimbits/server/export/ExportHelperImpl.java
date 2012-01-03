package com.nimbits.server.export;

import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.point.PointName;
import com.nimbits.client.model.value.Value;
import com.nimbits.server.intelligence.IntelligenceServiceFactory;

import java.util.Map;


public class ExportHelperImpl implements ExportHelper {

    public String exportPointDataToCSVSeparateColumns(final Map<PointName, Point> points) {
        final StringBuilder sb = new StringBuilder();
        int max = 0;
        int i = 0;
        Value v;
        Point p;

        for (final PointName name : points.keySet()) {
            sb.append(name.toString()).append(",,");
            if (points.get(name).getValues().size() > max) {
                max = points.get(name).getValues().size();
            }
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append("\n");


        while (i < max) {
            for (final PointName name : points.keySet()) {
                p = points.get(name);
                if (p.getValues().size() >= i) {
                    v = p.getValues().get(i);
                    sb.append(v.getTimestamp()).append(",").append(v.getNumberValue()).append(",");
                }
            }
            sb.deleteCharAt(sb.length() - 1);
            sb.append("\n");
            i++;
        }
        return sb.toString();
    }

    @Override
    public String exportPointDataToDescriptiveStatistics(final Map<PointName, Point> points) throws NimbitsException {
        final PointName pointName = points.keySet().iterator().next();
        final Point point = points.get(pointName);
        final StringBuilder sb = new StringBuilder();

        sb.append("{");
        for (final Value v : point.getValues()) {
            sb.append(v.getNumberValue()).append(",");
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append("}");
        return buildHTML(sb.toString(), pointName.getValue());
    }

    @Override
    public String exportPointDataToPossibleContinuation(final Map<PointName, Point> points) throws NimbitsException {
        final PointName pointName = points.keySet().iterator().next();
        final Point point = points.get(pointName);
        final StringBuilder sb = new StringBuilder();

        sb.append("{");
        for (final Value v : point.getValues()) {
            sb.append(v.getNumberValue()).append(",");
        }
        sb.append("...}");

        return buildHTML(sb.toString(), pointName.getValue());


    }

    private String buildHTML(final String request, final String header) throws NimbitsException {
        final String raw = IntelligenceServiceFactory.getInstance().getRawResult(request, "", true);
        final Map<String, String> html = IntelligenceServiceFactory.getInstance().getHTMLContent(raw);
        final StringBuilder hb = new StringBuilder();
        final String topHTML = addTopHTML(header);
        hb.append("<table border=\"0\">").append("<TR><TD width=\"50\"></TD><TD>");
        final String dataHtml = turnMapIntoHTML(html);

        //   hb.append(html.get("scripts").replace(";",";\n").replace("<script>","\n<script>\n")).append("\n");
        hb.append(topHTML)
                .append(dataHtml)
                .append("</td></TR></table>")
                .append("</BODY>")
                .append("\n")
                .append("</HTML>");

        return hb.toString();
    }

    private String addTopHTML(String header) {
        final StringBuilder hb = new StringBuilder();
        hb.append("<HTML>").append("\n");
        hb.append("<HEAD>").append("\n");

        hb.append("<link rel=\"stylesheet\" href=\"http://nimbits.net/assets/css/bootstrap-1.2.0.css\">").append("\n");
        hb.append("<link href=\"http://nimbits.net/assets/css/docs.css\" rel=\"stylesheet\">").append("\n");
        hb.append("<link href=\"http://nimbits.net/assets/js/google-code-prettify/prettify.css\" rel=\"stylesheet\">").append("\n");
        hb.append("<script src=\"http://code.jquery.com/jquery-1.5.2.min.js\"></script>").append("\n");
        hb.append("<script src=\"http://autobahn.tablesorter.com/jquery.tablesorter.min.js\"></script>").append("\n");
        hb.append("<script src=\"http://nimbits.net/assets/js/google-code-prettify/prettify.js\"></script>").append("\n");
        hb.append("<script src=\"http://nimbits.net/assets/js/util.js\"></script>").append("\n");

        hb.append("</HEAD>").append("\n");
        hb.append("<BODY>").append("\n");
        // hb.append(html.get("css").replace("<link","\n<link")).append("\n");
        hb.append("<h2>").append(header).append("<h2>");
        hb.append("<h3><a href=\"http://www.nimbits.com\">nimbits.com</a> Descriptive Statistics</h3>");
        return hb.toString();
    }

    private String turnMapIntoHTML(Map<String, String> html) {
        final StringBuilder hb = new StringBuilder();
        for (String s : html.keySet()) {
            if (!s.equals("css") && !s.equals("scripts")) {
                String h = html.get(s);
                if (!h.contains("Input:")) {  //input probably to large.
                    hb.append(h).append("\n");
                }
            }
        }
        return hb.toString()
                .replace("<a ", "<a style=\"display:none\" ")
                .replace("class=\"first btn\"", "style=\"display:none\"");
    }

}