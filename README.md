```
// Warehouse API Order:
{
  "id": 1,
  "state": "PARTIALLY_DELIVERED",
  "items": [
    { "code": "123", "amount": 15 },
    { "code": "456", "amount": 3 }
  ],
  "childOrder": {
    "id": 2,
    "state": "PARTIALLY_DELIVERED",
    "items": [
      { "code": "123", "amount": 5 },
      { "code": "456", "amount": 5 }
    ],
    "childOrder": {
      "id": 3,
      "state": "PARTIALLY_DELIVERED",
      "items": [
        { "code": "123", "amount": 6 },
        { "code": "456", "amount": 4 }
      ],
      "childOrder": {
          "id": 4,
          "state": "PARTIALLY_DELIVERED",
          "items": [
            { "code": "123", "amount": 1 },
            { "code": "456", "amount": 2 }
          ],
          "childOrder": {
              "id": 5,
              "state": "PENDING",
              "items": [
                { "code": "123", "amount": 3 },
                { "code": "456", "amount": 4 }
              ],
              "childOrder": null
          }
      }
    }
  }
}

// Core API Order before sync:
{
    "state": "PARTIALLY_DELIVERED",
    "vendorReferenceId": "1",
    "items": [
        { "code": "123", "quantity": 7, "finalizationReportItem": "FR1" },
        { "code": "123", "quantity": 8, "finalizationReportItem": "FR2" },
        { "code": "456", "quantity": 3, "finalizationReportItem": "FR2" },
    ],
    "child": {
        "state": "PARTIALLY_DELIVERED",
        "vendorReferenceId": "2",
        "items": [
            { "code": "123", "quantity": 5, "finalizationReportItem": "FR2" },
            { "code": "456", "quantity": 5, "finalizationReportItem": "FR2" }
        ],
        "child": {
            "state": "PENDING",
            "vendorReferenceId": "3",
            "items": [
                { "code": "123", "quantity": 10, "finalizationReportItem": "FR2" },
                { "code": "456", "quantity": 10, "finalizationReportItem": "FR2" }
            ],
            "child": null
        }
    }
}

// Core API Order after sync:
{
    "state": "PARTIALLY_DELIVERED",
    "vendorReferenceId": "1",
    "items": [
        { "code": "123", "quantity": 7, "finalizationReportItem": "FR1" },
        { "code": "123", "quantity": 8, "finalizationReportItem": "FR2" },
        { "code": "456", "quantity": 3, "finalizationReportItem": "FR2" },
    ],
    "child": {
        "state": "PARTIALLY_DELIVERED",
        "vendorReferenceId": "2",
        "items": [
            { "code": "123", "quantity": 5, "finalizationReportItem": "FR2" },
            { "code": "456", "quantity": 5, "finalizationReportItem": "FR2" }
        ],
        "child": {
            "state": "PARTIALLY_DELIVERED",
            "vendorReferenceId": "3",
            "items": [
                { "code": "123", "quantity": 6, "finalizationReportItem": "FR2" },
                { "code": "456", "quantity": 4, "finalizationReportItem": "FR2" },
                { "code": "123", "quantity": 1, "finalizationReportItem": "FR2" },
                { "code": "456", "quantity": 2, "finalizationReportItem": "FR2" }
            ],
            "child": {
                "state": "PENDING",
                "vendorReferenceId": "5",
                "items": [
                    { "code": "123", "quantity": 3, "finalizationReportItem": "FR2" },
                    { "code": "456", "quantity": 4, "finalizationReportItem": "FR2" }
                ],
                "child": null
            }
        }
    }
}
```