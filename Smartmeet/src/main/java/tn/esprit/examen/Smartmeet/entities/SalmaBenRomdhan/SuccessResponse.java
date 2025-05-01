package tn.esprit.examen.Smartmeet.entities.SalmaBenRomdhan;

// Helper classes for consistent JSON responses
public class SuccessResponse {
    Object data;

    public SuccessResponse(Object data) {
        this.data = data;
    }

    public Object getData() {
        return data;
    }
}
