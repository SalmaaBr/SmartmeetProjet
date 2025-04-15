package tn.esprit.examen.Smartmeet.entities.MaryemSalhi;

public enum TypeFeeling {
    EXCELLENT(5), GOOD(4), AVERAGE(3), BAD(2), TERRIBLE(1);

    private final int value;

    TypeFeeling(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }}
