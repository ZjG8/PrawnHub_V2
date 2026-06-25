# Fix History Screen

Problem:

ESP32 writes history to:

/ShrimpHub/history
    pushKey
        timestamp
        temperature
        tds
        turbidity
        waterLevel

Firebase contains records.

HistoryActivity receives Firebase updates but UI shows empty or partial results.

## Verify

1. Log total children count from:
   /ShrimpHub/history

2. Log every record:
   timestamp
   temperature
   tds
   turbidity
   waterLevel

3. Verify renderData() receives records.

4. Verify date filter compares Unix timestamps correctly.

5. Verify start/end DateTime picker uses milliseconds and not formatted strings.

6. If filtered count becomes zero, show:
   "No records found for selected range."

## Migration

Support ONLY this format:

/ShrimpHub/history
    pushKey
        timestamp
        temperature
        tds
        turbidity
        waterLevel

Remove dependency on:

time
param_type
value
rec_val
status

Keep backward compatibility optional.

## Charts

Build charts directly from:

temperature
tds
turbidity
waterLevel

using timestamp as X-axis.

## Validation

Display:

Firebase records loaded: X
Records after filter: X
Oldest timestamp: X
Newest timestamp: X

to quickly diagnose filter issues.

Goal:
History list and graphs must display all records written by ESP32 from /ShrimpHub/history.