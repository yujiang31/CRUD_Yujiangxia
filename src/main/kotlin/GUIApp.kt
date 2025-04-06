import javafx.application.Application
import javafx.geometry.Insets
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.layout.VBox
import javafx.stage.Stage


class GUIApp : Application() {

    private var currentOffset = 0 // Para paginar la lista de clientes

    override fun start(stage: Stage) {

        // Definimos otro ComboBox para primero Seleccionar que tabla se hara modificaciones
        val TableSelector = ComboBox<String>()

        // Opciones Tablas Disponibles a escoger (Inventory y Categories)
        TableSelector.items.addAll("Inventory", "Categories")
        TableSelector.value = "Selecciona una Tabla"

        // ComboBox Secundario (Operaciones)
        val operationSelector = ComboBox<String>()
        operationSelector.items.addAll("Insertar", "Actualizar", "Eliminar", "Listar")
        operationSelector.value = "Selecciona una Operación"

        // Campos Reutilizables Tabla "Inventory"
        val Prod_id = TextField().apply { promptText = "ID Producto" }
        val Stock = TextField().apply { promptText = "Cantidad Stock" }
        val Sales = TextField().apply { promptText = "Número Ventas" }

        // Campos Reutilizables Tabla "Categories"
        val Category = TextField().apply { promptText = "Número Categoría" }
        val CategoryName = TextField().apply { promptText = "Nombre Categoría" }

        val output = Label()
        val executeButton = Button("Ejecutar operación")

        val container = VBox(10.0)
        container.padding = Insets(20.0)

        // Menu principal dependiendo de que selección habra cambiado las opciones
        fun updateForm(selectedTable: String, operation: String) {
            container.children.clear()  // Limpiar el contenedor antes de actualizar los campos
            container.children.addAll(TableSelector, operationSelector)
            output.text = ""
            currentOffset = 0

            when (selectedTable) {
                "Inventory" -> when (operation) {
                    "Insertar" -> container.children.addAll(Prod_id, Stock, Sales, executeButton, output)
                    "Actualizar" -> container.children.addAll(Prod_id, Stock, executeButton, output)
                    "Eliminar" -> container.children.addAll(Prod_id, executeButton, output)
                    "Listar" -> container.children.addAll(executeButton, output)
                }

                "Categories" -> when (operation) {
                    "Insertar" -> container.children.addAll(Category, CategoryName, executeButton, output)
                    "Actualizar" -> container.children.addAll(Category, CategoryName, executeButton, output)
                    "Eliminar" -> container.children.addAll(Category, executeButton, output)
                    "Listar" -> container.children.addAll(executeButton, output)
                }
            }
        }

        // Acción del botón principal
        executeButton.setOnAction {
            val tabla = TableSelector.value
            val operacion = operationSelector.value

            when (tabla) {
                "Inventory" -> when (operacion) {
                    "Insertar" -> {
                        val id = Prod_id.text.toIntOrNull()
                        val stock = Stock.text.toIntOrNull()
                        val sales = Sales.text.toIntOrNull()
                        if (id != null && stock != null && sales != null) {
                            insertInventory(id, stock, sales)
                            output.text = "✅ Inventario insertado correctamente."
                            Prod_id.clear(); Stock.clear(); Sales.clear()
                        } else output.text = "❌ Campos inválidos."
                    }
                    "Actualizar" -> {
                        val id = Prod_id.text.toIntOrNull()
                        val stock = Stock.text.toIntOrNull()
                        if (id != null && stock != null) {
                            updateInventoryStock(id, stock)
                            output.text = "✅ Stock actualizado."
                            Prod_id.clear(); Stock.clear()
                        } else output.text = "❌ ID o Stock inválido."
                    }
                    "Eliminar" -> {
                        val id = Prod_id.text.toIntOrNull()
                        if (id != null) {
                            deleteInventory(id)
                            output.text = "✅ Inventario eliminado."
                            Prod_id.clear()
                        } else output.text = "❌ ID inválido."
                    }
                    "Listar" -> {
                        output.text = listInventory(offset = currentOffset)
                        currentOffset += 10
                    }
                }

                "Categories" -> when (operacion) {
                    "Insertar" -> {
                        val id = Category.text.toIntOrNull()
                        val name = CategoryName.text
                        if (id != null && name.isNotBlank()) {
                            insertCategory(id, name)
                            output.text = "✅ Categoría insertada correctamente."
                            Category.clear(); CategoryName.clear()
                        } else output.text = "❌ Datos inválidos."
                    }
                    "Actualizar" -> {
                        val id = Category.text.toIntOrNull()
                        val name = CategoryName.text
                        if (id != null && name.isNotBlank()) {
                            updateCategory(id, name)
                            output.text = "✅ Categoría actualizada."
                            Category.clear(); CategoryName.clear()
                        } else output.text = "❌ ID o nombre inválido."
                    }
                    "Eliminar" -> {
                        val id = Category.text.toIntOrNull()
                        if (id != null) {
                            deleteCategory(id)
                            output.text = "✅ Categoría eliminada."
                            Category.clear()
                        } else output.text = "❌ ID inválido."
                    }
                    "Listar" -> {
                        output.text = listCategories(offset = currentOffset)
                        currentOffset += 10
                    }
                }
            }
        }

        // Cambiar formulario al cambiar operación
        TableSelector.setOnAction {
            updateForm(TableSelector.value, operationSelector.value)
        }

        operationSelector.setOnAction {
            updateForm(TableSelector.value, operationSelector.value)
        }

        // Mostrar formulario inicial
        updateForm(TableSelector.value, operationSelector.value)

        // Mostrar ventana
        stage.scene = Scene(container, 420.0, 400.0)
        stage.title = "Gestión de Inventario y Categorías (CRUD) :)"
        stage.show()
    }

    fun listCategories(offset: Int = 0): String {
        val sql = "SELECT category, categoryname FROM categories ORDER BY category ASC LIMIT 10 OFFSET $offset"
        val builder = StringBuilder("📋 Categorías:\n")

        try {
            Database.getConnection()?.use { conn ->
                conn.createStatement().use { stmt ->
                    val rs = stmt.executeQuery(sql)
                    var found = false
                    while (rs.next()) {
                        found = true
                        // Decodificar los datos BYTEA a texto (asumiendo que están en UTF-8)
                        val categoryNameBytes = rs.getBytes("categoryname")
                        val categoryName = String(categoryNameBytes, Charsets.UTF_8)

                        // Mostrar el ID y el nombre de la categoría
                        builder.append("${rs.getInt("category")} - $categoryName\n")
                    }
                    if (!found) {
                        builder.append("No hay más categorías.\n")
                        currentOffset = 0
                    }
                }
            }
        } catch (e: Exception) {
            return "❌ Error al consultar categorías: ${e.message}"
        }
        return builder.toString()
    }

}


