
fun insertInventory(id: Int, stock: Int, sales: Int) {
    val sql = "INSERT INTO inventory (prod_id, quan_in_stock, sales) VALUES (?, ?, ?)"
    Database.getConnection()?.use { conn ->
        conn.prepareStatement(sql).use {
            it.setInt(1, id)
            it.setInt(2, stock)
            it.setInt(3, sales)
            it.executeUpdate()
            println("✅ Inventario insertado.")
        }
    }
}

fun updateInventoryStock(id: Int, newStock: Int) {
    val sql = "UPDATE inventory SET quan_in_stock = ? WHERE prod_id = ?"
    Database.getConnection()?.use { conn ->
        conn.prepareStatement(sql).use {
            it.setInt(1, newStock)
            it.setInt(2, id)
            it.executeUpdate()
            println("✅ Stock actualizado.")
        }
    }
}

fun deleteInventory(id: Int) {
    val sql = "DELETE FROM inventory WHERE prod_id = ?"
    Database.getConnection()?.use { conn ->
        conn.prepareStatement(sql).use {
            it.setInt(1, id)
            it.executeUpdate()
            println("✅ Inventario eliminado.")
        }
    }
}

fun listInventory(offset: Int = 0): String {
    val sql = "SELECT prod_id, quan_in_stock, sales FROM inventory ORDER BY prod_id ASC LIMIT 10 OFFSET $offset"
    val builder = StringBuilder("📦 Inventario:\n")
    try {
        Database.getConnection()?.use { conn ->
            conn.createStatement().use { stmt ->
                val rs = stmt.executeQuery(sql)
                var found = false
                while (rs.next()) {
                    found = true
                    builder.append("Producto ${rs.getInt("productid")} - Stock: ${rs.getInt("stock")} - Ventas: ${rs.getInt("sales")}\n")
                }
                if (!found) {
                    builder.append("No hay más productos")
                }
            }
        }
    } catch (e: Exception) {
        return "❌ Error al listar inventario: ${e.message}"
    }
    return builder.toString()
}

