package Logic;

import Piece.Piece;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Save {

    public static void saveGame(Game game) {
        // Création du fichier JSON avec les données du jeu
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("currentColor", game.currentColor);
        jsonObject.put("gameMode", game.gameMode);
        jsonObject.put("whiteTimeRemaining", game.timers.getWhiteTimeRemaining());
        jsonObject.put("blackTimeRemaining", game.timers.getBlackTimeRemaining());
        jsonObject.put("stalemate", game.stalemate);
        jsonObject.put("promotion", game.promotion);

        JSONArray jsonPieces = new JSONArray();
        for (Piece piece : game.pieces) {
            jsonPieces.put(piece.toJson());
        }
        jsonObject.put("pieces", jsonPieces);

        JSONArray jsonSimPieces = new JSONArray();
        for (Piece piece : game.simPieces) {
            jsonSimPieces.put(piece.toJson());
        }
        jsonObject.put("simPieces", jsonSimPieces);

        JSONArray jsonHistorize = new JSONArray();
        for (String move : game.Historizes) {
            jsonHistorize.put(move);
        }
        jsonObject.put("historize", jsonHistorize);

        // Ouvrir un explorateur de fichiers pour permettre à l'utilisateur de choisir l'emplacement et le nom du fichier
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Enregistrer la sauvegarde");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Fichiers JSON", "json"));

        int userSelection = fileChooser.showSaveDialog(null);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();

            // Ajouter l'extension .json si elle est manquante
            String filePath = fileToSave.getAbsolutePath();
            if (!filePath.endsWith(".json")) {
                filePath += ".json";
            }

            // Écrire les données JSON dans le fichier choisi par l'utilisateur
            try (FileWriter file = new FileWriter(filePath)) {
                file.write(jsonObject.toString(4));
                System.out.println("Sauvegarde réussie : " + filePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void loadSave(Game game) {

        // Ouvrir un explorateur de fichiers pour sélectionner le fichier de sauvegarde
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Charger une sauvegarde");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Fichiers JSON", "json"));

        int userSelection = fileChooser.showOpenDialog(null);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToLoad = fileChooser.getSelectedFile();
            String filePath = fileToLoad.getAbsolutePath();

            try (FileReader reader = new FileReader(filePath)) {
                StringBuilder jsonContent = new StringBuilder();
                int i;
                while ((i = reader.read()) != -1) {
                    jsonContent.append((char) i);
                }

                JSONObject jsonObject = new JSONObject(jsonContent.toString());
                // Vérification des clés essentielles pour s'assurer que c'est une sauvegarde valide de jeu d'échecs
                if (isValidChessSave(jsonObject)) {
                    // Charger les données du fichier JSON
                    game.currentColor = jsonObject.getInt("currentColor");
                    game.gameMode = jsonObject.getInt("gameMode");
                    game.timers.setWhiteTimeRemaining(jsonObject.getInt("whiteTimeRemaining"));
                    game.timers.setBlackTimeRemaining(jsonObject.getInt("blackTimeRemaining"));
                    game.stalemate = jsonObject.getBoolean("stalemate");
                    game.promotion = jsonObject.getBoolean("promotion");

                    // Charger les pièces
                    JSONArray jsonPieces = jsonObject.getJSONArray("pieces");
                    game.pieces.clear();
                    for (int j = 0; j < jsonPieces.length(); j++) {
                        JSONObject jsonPiece = jsonPieces.getJSONObject(j);
                        Piece piece = Piece.fromJson(jsonPiece);
                        game.pieces.add(piece);

                    }

                    // Charger les pièces simulées
                    JSONArray jsonSimPieces = jsonObject.getJSONArray("simPieces");
                    game.simPieces.clear();
                    for (int j = 0; j < jsonSimPieces.length(); j++) {
                        JSONObject jsonSimPiece = jsonSimPieces.getJSONObject(j);
                        Piece simPiece = Piece.fromJson(jsonSimPiece);
                        game.simPieces.add(simPiece);
                    }

                    // Charger l'historique des mouvements
                    JSONArray jsonHistorize = jsonObject.getJSONArray("historize");
                    game.Historizes.clear();
                    for (int j = 0; j < jsonHistorize.length(); j++) {
                        game.Historizes.add(jsonHistorize.getString(j));
                    }

                } else {
                    System.out.println("Le fichier sélectionné n'est pas une sauvegarde de jeu d'échecs valide.");
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static boolean isValidChessSave(JSONObject jsonObject) {
        try {
            // Vérification des clés principales et de leur type attendu
            return  jsonObject.has("currentColor") && jsonObject.get("currentColor") instanceof Integer
                    && jsonObject.has("gameMode") && jsonObject.get("gameMode") instanceof Integer
                    && jsonObject.has("whiteTimeRemaining") && jsonObject.get("whiteTimeRemaining") instanceof Integer
                    && jsonObject.has("blackTimeRemaining") && jsonObject.get("blackTimeRemaining") instanceof Integer
                    && jsonObject.has("stalemate") && jsonObject.get("stalemate") instanceof Boolean
                    && jsonObject.has("promotion") && jsonObject.get("promotion") instanceof Boolean
                    && jsonObject.has("pieces") && jsonObject.get("pieces") instanceof JSONArray
                    && jsonObject.has("simPieces") && jsonObject.get("simPieces") instanceof JSONArray
                    && jsonObject.has("historize") && jsonObject.get("historize") instanceof JSONArray;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


}
