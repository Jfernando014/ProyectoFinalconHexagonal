package co.edu.unicauca.usuarios.models.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum TipoDocente {
    PLANTA, OCASIONAL, CATEDRA;

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static TipoDocente from(String v){
        if (v == null) return null;
        String s = v.trim().toUpperCase();
        return switch (s) {
            case "PLANTA" -> PLANTA;
            case "OCASIONAL" -> OCASIONAL;
            case "CATEDRA", "CÁTEDRA", "CATEDRATICO", "CATEDRÁTICO" -> CATEDRA;
            default -> throw new IllegalArgumentException(
                    "tipoDocente inválido: " + v + " (válidos: PLANTA, OCASIONAL, CATEDRA)"
            );
        };
    }
}