package com.example.demo.utils.validator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;

import lombok.Data;

@Data
public class ValidateImportUser {

    // public static getValueColumn(Cell cell){
    // switch (cell.getCellType()) {
    // case CellType.NUMERIC:
    // return cell.getNumericCellValue();;
    // case CellType.STRING:
    // return cell.getStringCellValue();
    // default:
    // return cell.getBooleanCellValue();
    // }
    // }

    public static String validateUsername(String username) {
        if (username == null) {
            return "username không được để trống";
        }
        return null;
    }

    public static String validateRole(String role) {
        if (role == null) {
            return "role không được để trống.";
        }
        return null;
    }

    public static List<String> validateRow(Row row, Map<String, Integer> headerMapValue) {

        Integer indexUsername = headerMapValue.get("Tên");
        Integer indexRole = headerMapValue.get("Vai trò");

        String username = row.getCell(indexUsername) != null ? row.getCell(indexUsername).getStringCellValue(): null;
        String role = row.getCell(indexRole) != null ? row.getCell(indexRole).getStringCellValue(): null;
        List<String> errorRow = new ArrayList<>();
        String validateUsername = validateUsername(username);
        String validateRole = validateRole(role);
        errorRow.add(validateUsername == null ? null : validateUsername);
        errorRow.add(validateRole == null ? null : validateRole);

        List<String> filterError = errorRow.stream().filter(error -> error != null).toList();
        return filterError;
    }
}
