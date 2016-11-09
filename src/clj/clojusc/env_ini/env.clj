(ns clojusc.env-ini.env
  (:require [clojure.string :as string]
            [clojusc.env-ini.common-env :as common]
            [clojusc.env-ini.util :as util])
  (:import [clojure.lang Keyword])
  (:refer-clojure :exclude [get read]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Wrapper for System/getenv   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defmulti get-env
  (fn ([& args]
    (mapv class (into [] args)))))

(defmethod get-env [String] [key]
  (System/getenv (common/str->envstr key)))

(defmethod get-env [Keyword] [key]
  (System/getenv (common/keyword->envstr key)))

(defmethod get-env [Keyword Keyword] [section key]
  (System/getenv (common/section-key->env section key)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   ENV Reader   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn read-env
  ""
  [& {:keys [keywordize?]
      :or {keywordize? true}}]
  (if keywordize?
    (common/envstrs->keywords (System/getenv))
    (System/getenv)))

(def memoized-read-env (memoize read-env))

(defn read
  [& {:keys [force-reload? keywordize?]
      :or {force-reload? false keywordize? true}
      :as all-args}]
  (let [args (flatten (into [] (dissoc all-args :force-reload?)))]
    (if force-reload?
      (apply read-env args)
      (apply memoized-read-env args))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   INI Operations Against Data   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn get
  ""
  ([data key]
    (get-in data [:env key]))
  ([data section key]
    (get-in data [:env (common/section-key->env section key)])))
