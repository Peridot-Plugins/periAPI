package api.peridot.periapi.utils.replacements;

public class Replacement {

    private final String from;
    private final String to;

    public Replacement(String from, Object to) {
        this.from = from;
        this.to = to.toString();
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

}
