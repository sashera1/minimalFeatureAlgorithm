import java.util.Map;
class Phoneme {
    private final String name;
    private Map<String, Boolean> features;

    public Phoneme(String name, Map<String, Boolean> features) {
        this.name = name;
        this.features = features;
    }

    public String getName() {
        return name;
    }

    public Map<String, Boolean> getFeatures() {
        return features;
    }

    @Override
    public String toString() {
        return "Phoneme{name='" + name + "', features=" + features + "}";
    }
}