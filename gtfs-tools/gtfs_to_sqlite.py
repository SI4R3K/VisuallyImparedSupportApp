import sqlite3
import zipfile
import csv
import io
import os


DB_NAME = "gtfs.db"
GTFS_ZIP = "GTFS.zip"


# ---------- DB SETUP ----------
def create_tables(conn):
    cur = conn.cursor()

    cur.execute("""
    CREATE TABLE IF NOT EXISTS stops (
        stop_id TEXT NOT NULL PRIMARY KEY,
        stop_code TEXT NOT NULL,
        stop_name TEXT NOT NULL
    )
    """)

    cur.execute("""
    CREATE TABLE IF NOT EXISTS routes (
        route_id TEXT NOT NULL PRIMARY KEY,
        route_short_name TEXT NOT NULL
    )
    """)

    cur.execute("""
    CREATE TABLE IF NOT EXISTS trips (
        trip_id TEXT NOT NULL PRIMARY KEY,
        route_id TEXT NOT NULL,
        service_id TEXT NOT NULL,
        trip_headsign TEXT NOT NULL
    )
    """)

    cur.execute("""
    CREATE TABLE stop_times (
        trip_id TEXT NOT NULL,
        departure_time TEXT NOT NULL,
        stop_id TEXT NOT NULL,
        stop_sequence INTEGER NOT NULL,
        PRIMARY KEY (trip_id, stop_id, stop_sequence)
    )
    """)

    cur.execute("""
    CREATE TABLE calendar_dates (
        service_id TEXT NOT NULL,
        date TEXT NOT NULL,
        PRIMARY KEY (service_id, date)
    )
    """)

    conn.commit()


# ---------- HELPERS ----------
def read_csv_from_zip(zip_file, filename):
    with zip_file.open(filename) as f:
        return list(csv.DictReader(
            io.TextIOWrapper(f, encoding="utf-8-sig")
            ))


def insert_batch(conn, query, rows):
    cur = conn.cursor()
    cur.executemany(query, rows)
    conn.commit()


# ---------- IMPORT ----------
def import_stops(zip_file, conn):
    print("Importing stops...")

    rows = read_csv_from_zip(zip_file, "stops.txt")

    data = [
        (
            r["stop_id"],
            r.get("stop_code"),
            r.get("stop_name")
        )
        for r in rows
    ]

    insert_batch(conn,
        "INSERT OR REPLACE INTO stops VALUES (?, ?, ?)",
        data
    )


def import_routes(zip_file, conn):
    print("Importing routes...")

    rows = read_csv_from_zip(zip_file, "routes.txt")

    data = [
        (
            r["route_id"],
            r.get("route_short_name")
        )
        for r in rows
    ]

    insert_batch(conn,
        "INSERT OR REPLACE INTO routes VALUES (?, ?)",
        data
    )


def import_trips(zip_file, conn):
    print("Importing trips...")

    rows = read_csv_from_zip(zip_file, "trips.txt")

    data = [
        (
            r["trip_id"],
            r["route_id"],
            r["service_id"],
            r.get("trip_headsign")
        )
        for r in rows
    ]

    insert_batch(conn,
        "INSERT OR REPLACE INTO trips VALUES (?, ?, ?, ?)",
        data
    )


def import_stop_times(zip_file, conn):
    print("Importing stop_times... (this is the biggest table)")

    rows = read_csv_from_zip(zip_file, "stop_times.txt")

    data = [
        (
            r["trip_id"],
            r.get("departure_time"),
            r["stop_id"],
            int(r.get("stop_sequence", 0))
        )
        for r in rows
    ]

    insert_batch(conn,
        """
        INSERT INTO stop_times
        (trip_id, departure_time, stop_id, stop_sequence)
        VALUES (?, ?, ?, ?)
        """,
        data
    )

def import_calendar_dates(zip_file, conn):
    print("Importing calendar...")

    rows = read_csv_from_zip(zip_file, "calendar_dates.txt")

    data = [
        (
            r["service_id"],
            r["date"]
        )
        for r in rows
    ]

    insert_batch(conn,
        """
        INSERT INTO calendar_dates
        (service_id, date)
        VALUES (?, ?)
        """,
        data
    )


# ---------- MAIN ----------
def main():
    if not os.path.exists(GTFS_ZIP):
        print("gtfs.zip not found!")
        return

    conn = sqlite3.connect(DB_NAME)

    create_tables(conn)

    with zipfile.ZipFile(GTFS_ZIP, 'r') as zip_file:
        import_stops(zip_file, conn)
        import_routes(zip_file, conn)
        import_trips(zip_file, conn)
        import_stop_times(zip_file, conn)
        import_calendar_dates(zip_file, conn)

    conn.cursor().execute("CREATE INDEX IF NOT EXISTS idx_stop_times_stop_id ON stop_times(stop_id)")
    conn.cursor().execute("CREATE INDEX IF NOT EXISTS idx_stop_times_trip_id ON stop_times(trip_id)")
    conn.close()

    print("DONE -> gtfs.db created")


if __name__ == "__main__":
    main()