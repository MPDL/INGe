
--
-- TOC entry 1764 (class 1259 OID 29607)
-- Dependencies: 1493
-- Name: ix_lang; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX ix_lang ON triples USING btree (lang);


--
-- TOC entry 1765 (class 1259 OID 29608)
-- Dependencies: 1493
-- Name: ix_model; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX ix_model ON triples USING btree (model);


--
-- TOC entry 1766 (class 1259 OID 29609)
-- Dependencies: 1493
-- Name: ix_object; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX ix_object ON triples USING btree (object);


--
-- TOC entry 1767 (class 1259 OID 29610)
-- Dependencies: 1493
-- Name: ix_predicate; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX ix_predicate ON triples USING btree (predicate);


--
-- TOC entry 1768 (class 1259 OID 29611)
-- Dependencies: 1493
-- Name: ix_subject; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX ix_subject ON triples USING btree (subject);


--
-- TOC entry 1763 (class 1259 OID 29612)
-- Dependencies: 1492 1492 1492
-- Name: pk; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE UNIQUE INDEX pk ON results USING btree (id, value, lang);


--
-- TOC entry 1769 (class 1259 OID 29613)
-- Dependencies: 1493 1493 1493 1493 1493
-- Name: pk_all; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE UNIQUE INDEX pk_all ON triples USING btree (model, subject, predicate, object, lang);


--
-- TOC entry 1762 (class 1259 OID 29614)
-- Dependencies: 1490 1490 1490 1490
-- Name: pk_matches; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE UNIQUE INDEX pk_matches ON matches USING btree (id, value, lang, model);

