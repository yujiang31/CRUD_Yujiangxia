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
        TableSelector.value = "Seleciona una Tabla"


        // ComboBox Secundario (Operaciones)
        val operationSelector = ComboBox<String>()
        operationSelector.items.addAll("Insertar", "Actualizar", "Eliminar", "Listar")
        operationSelector.value = "Seleciona una Operacion"




        // Campos Reutilizables Tabla "Inventory"
        val Prod_id = TextField().apply { promptText = "ID Producto" }
        val Stock = TextField().apply { promptText = "Cantidad Stock" }
        val Sales = TextField().apply { promptText = "Numero Ventas" }



        // Campos Reutilizables Tabla "Categories"
        val Category = TextField().apply { promptText = "Numero Categoria" }
        val CategoryName = TextField().apply { promptText = "Nombre Categoria" }




        val output = Label()
        val executeButton = Button("Ejecutar operación")

        val container = VBox(10.0)
        container.padding = Insets(20.0)

        // Menu principal dependiendo de que seleccion habra cambiado las opciones

        fun updateForm(selectedTable: String, Operation: String) {
            container.children.setAll(TableSelector, operationSelector)
            when (selectedTable) {
                "Inventory" -> when(Operation){
                    "Insertar" -> container.children.addAll(Prod_id, Stock, Sales)
                    "Actualizar" -> container.children.addAll(Prod_id, Stock)
                    "Eliminar" -> container.children.addAll(Prod_id)
                    "Listar" -> container.children.addAll(executeButton, output)

                }

                "Categories" -> when(Operation){
                    "Insertar" -> container.children.addAll(Category, CategoryName)
                    "Actualizar" -> container.children.addAll(Category, CategoryName)
                    "Eliminar" -> container.children.addAll(Category, CategoryName)
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

        // Mostrar formulario inicial
        updateForm(TableSelector.value, operationSelector.value)

        // Mostrar ventana
        stage.scene = Scene(container, 420.0, 400.0)
        stage.title = "Gestión de Inventario y Categorias (CRUD) :)"
        stage.show()
    }


}