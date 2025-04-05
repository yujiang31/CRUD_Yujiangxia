fun insertCategory(id: Int, name: String) {
    val sql = "INSERT INTO categories (category, categoryname) VALUES (?, ?)"
    Database.getConnection()?.use { conn ->
        conn.prepareStatement(sql).use {
            it.setInt(1, id)
            it.setString(2, name)
            it.executeUpdate()
            println("✅ Categoría insertada.")
        }
    }
}

fun updateCategory(id: Int, newName: String) {
    val sql = "UPDATE categories SET categoryname = ? WHERE category = ?"
    Database.getConnection()?.use { conn ->
        conn.prepareStatement(sql).use {
            it.setString(1, newName)
            it.setInt(2, id)
            it.executeUpdate()
            println("✅ Categoría actualizada.")
        }
    }
}

fun deleteCategory(id: Int) {
    val sql = "DELETE FROM categories WHERE category = ?"
    Database.getConnection()?.use { conn ->
        conn.prepareStatement(sql).use {
            it.setInt(1, id)
            it.executeUpdate()
            println("✅ Categoría eliminada.")
        }
    }
}

fun listCategories(offset: Int = 0): String {
    val sql = "SELECT category, categoryname FROM categories ORDER BY category ASC LIMIT 10 OFFSET $offset"
    val builder = StringBuilder("🏷️ Categorías:\n")
    try {
        Database.getConnection()?.use { conn ->
            conn.createStatement().use { stmt ->
                val rs = stmt.executeQuery(sql)
                var found = false
                while (rs.next()) {
                    found = true
                    builder.append("${rs.getInt("categoryid")} - ${rs.getString("categoryname")}\n")
                }
                if (!found) {
                    builder.append("No hay más categorías.\n")
                }
            }
        }
    } catch (e: Exception) {
        return "❌ Error al listar categorías: ${e.message}"
    }
    return builder.toString()
}
