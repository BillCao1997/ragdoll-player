import java.awt.geom.Point2D;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Vector;
import org.json.simple.*;
import org.json.simple.parser.*;

public class PoseState {
    private static int count = 0;
    public Movable[] parts = new Movable[14]; // All movables we're managing
    public double[] thetas = new double[14];
    public double[] factors = new double[14];
    public double torsoX = 0;
    public double torsoY = 0;

    public PoseState() { count = 0; }

    public void addPart(Movable comp) {
        for (Movable m : comp.children) {
            this.addPart(m);
        }
        parts[count] = comp;
        thetas[count] = comp.thetaNow;
        factors[count] = comp.scaleFactor;
        count++;
        if (comp.getParent() != null) return;
        // No parent, this is torso
        Point2D pos = comp.getPosition();
        torsoX = (int)pos.getX();
        torsoY = (int)pos.getY();
    }

    public void loadStoredPose() {
        for (int i = 0; i < 14; ++i) {
            parts[i].setTheta(thetas[i]);
            parts[i].setScaleFactor(factors[i]);
        }
        parts[13].setPosition(torsoX, torsoY);
    }

    public boolean saveToFile(String path) {
        JSONArray arr = new JSONArray();
        for (int i = 0; i < 14; ++i) {
            JSONObject obj = new JSONObject();
            obj.put("theta", thetas[i]);
            obj.put("factor", factors[i]);
            arr.add(obj);
        }
        // Position of torso
        JSONObject obj = new JSONObject();
        obj.put("torsoX", torsoX);
        obj.put("torsoY", torsoY);
        arr.add(obj);

        StringWriter out = new StringWriter();
        try {
            arr.writeJSONString(out);
            String jsonText = out.toString();
            PrintWriter writer = new PrintWriter(path, "UTF-8");
            writer.print(jsonText);
            writer.close();
            return true;
        }
        catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public String loadFile(String path) {
        try {
            byte[] encoded = Files.readAllBytes(Paths.get(path));
            String data = new String(encoded, "UTF-8");
            JSONParser parser = new JSONParser();
            Object obj = parser.parse(data);
            JSONArray array = (JSONArray) obj;

            int n = array.size() - 1;
            for (int i = 0; i < n; ++i) {
                JSONObject obj2 = (JSONObject)array.get(i);
                double theta = (double)obj2.get("theta");
                double factor = (double)obj2.get("factor");
                parts[i].setTheta(theta);
                parts[i].setScaleFactor(factor);
            }

            JSONObject obj2 = (JSONObject)array.get(n);
            torsoX = (double)obj2.get("torsoX");
            torsoY = (double)obj2.get("torsoY");
            parts[n - 1].setPosition(torsoX, torsoY);

            return null;
        } catch (FileNotFoundException e) {
            return "File not found.";
        } catch (IOException e) {
            e.printStackTrace();
            return "IOException, check log.";
        } catch (ParseException e) {
            return "Invalid file format. Your file may be corrupted";
        } catch (Exception e) {
            e.printStackTrace();
            return "Non-Java Exception. Please try again loading the file.";
        }
    }

    public String toString() {
        String s = "";
        for (int i = 0; i < 13; ++i) {
            s += parts[i].toString() + "\n";
        }
        s += "[Torso] X = " + torsoX + ", Y = " + torsoY;
        return s;
    }
}
