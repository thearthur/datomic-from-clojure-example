(ns illu.core
  (:use [datomic.api :only [db q] :as d]))

(def uri "datomic:dev://localhost:4334/illustrators")

(defn make-db []
  (d/create-database uri))

(defn delete-db []
  (d/delete-database uri))

(def conn (d/connect uri))

(defn add-person-attribute []
  (d/transact conn [{:db/id #db/id[:db.part/db]
                     :db/ident :person/name
                     :db/valueType :db.type/string
                     :db/cardinality :db.cardinality/one
                     :db/doc "A person's name"
                     :db.install/_attribute :db.part/db}]))

(defn add-address-attribute []
  (d/transact conn [{:db/id #db/id[:db.part/db]
                     :db/ident :person/address
                     :db/valueType :db.type/string
                     :db/cardinality :db.cardinality/one
                     :db/doc "A person's Address"
                     :db.install/_attribute :db.part/db}]))

(defn add-a-person [name]
  (d/transact conn [{:db/id #db/id[:db.part/user] :person/name name}]))

(defn get-all-people []
  (q '[:find ?n :where [?c person/name ?n ]] (db conn)))

(defn find-person [name dbc]
  (first (first (q `[:find ?c :where [?c person/name ~name]] dbc))))

(defn illustrators-that-live-with [name]
  (q `[:find ?n1 :where
       [?c person/name ~name]
       [?c person/address ?a]
       [?n person/name ?n1]]  (db conn)))

(defn illustrators-that-live-together []
  (q `[:find ?n1 ?n :where
       [?c person/name ?n]
       [?c person/address ?a]
       [?c1 person/name ?n1]
       [?c1 person/address ?a]]  (db conn)))

(defn add-address [name address]
  (let [dbc (db conn)
        id (find-person name dbc)]
    (d/transact conn [{:db/id id :person/address address}])))