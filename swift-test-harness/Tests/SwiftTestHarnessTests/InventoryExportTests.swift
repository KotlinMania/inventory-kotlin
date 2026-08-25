import Testing
import Inventory

@Suite("Inventory Export Tests")
struct InventoryExportTests {
    @Test("Swift module loads and imports cleanly")
    func swiftModuleLoads() {
        #expect(Bool(true))
    }
}
