fun insertCategory(id: Int, name: String) {
    val sql = "INSERT INTO categories (category, categoryname) VALUES (?, ?)"
    Database.getConnection()?.use { conn ->
        conn.prepareStatement(sql).use {
            it.setInt(1, id)
            it.setBytes(2, name.toByteArray(Charsets.UTF_8))
            it.executeUpdate()
            println("✅ Categoría insertada.")
        }
    }
}

fun updateCategory(id: Int, newName: String) {
    val sql = "UPDATE categories SET categoryname = ? WHERE category = ?"
    Database.getConnection()?.use { conn ->
        conn.prepareStatement(sql).use {
            it.setBytes(1, newName.toByteArray(Charsets.UTF_8))
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



