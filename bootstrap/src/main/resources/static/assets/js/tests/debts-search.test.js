const test = require("node:test");
const assert = require("node:assert/strict");

const {
    filterAllocatableDebts,
    getSelectedAllocatableStatuses
} = require("../debts-search.js");

test("debts-search filters debts to allocatable statuses", () => {
    const debts = [
        { id: "1", status: "OPEN" },
        { id: "2", status: "PARTIALLY_PAID" },
        { id: "3", status: "PAID" }
    ];

    const result = filterAllocatableDebts(debts);
    assert.deepEqual(result.map((debt) => debt.id), ["1", "2"]);
});

test("debts-search keeps only allowed selected statuses", () => {
    const selected = getSelectedAllocatableStatuses(["OPEN", "PAID", "PARTIALLY_PAID"]);
    assert.deepEqual(selected, ["OPEN", "PARTIALLY_PAID"]);
});
