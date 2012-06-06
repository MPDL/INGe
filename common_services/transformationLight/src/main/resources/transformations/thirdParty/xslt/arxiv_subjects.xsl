<?xml version="1.0" encoding="UTF-8"?>
<!--
	CDDL HEADER START
	
	The contents of this file are subject to the terms of the
	Common Development and Distribution License, Version 1.0 only
	(the "License"). You may not use this file except in compliance
	with the License.
	
	You can obtain a copy of the license at license/ESCIDOC.LICENSE
	or http://www.escidoc.de/license.
	See the License for the specific language governing permissions
	and limitations under the License.
	
	When distributing Covered Code, include this CDDL HEADER in each
	file and include the License file at license/ESCIDOC.LICENSE.
	If applicable, add the following below this CDDL HEADER, with the
	fields enclosed by brackets "[]" replaced with your own identifying
	information: Portions Copyright [yyyy] [name of copyright owner]
	
	CDDL HEADER END
	
	
	Copyright 2006 - 2009 Fachinformationszentrum Karlsruhe Gesellschaft
	für wissenschaftlich-technische Information mbH and Max-Planck-
	Gesellschaft zur Förderung der Wissenschaft e.V.
	All rights reserved. Use is subject to license terms.
--> 
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:template name="arxiv_subjects">
		<xsl:param name="subject"/>
		<xsl:choose>		
					<xsl:when test="$subject = 'astro-ph'">Astrophysics, astro-ph</xsl:when>
					<xsl:when test="$subject = 'astro-ph.CO'">Astrophysics, Cosmology and Extragalactic Astrophysics, astro-ph.CO</xsl:when>
					<xsl:when test="$subject = 'astro-ph.EP'"> Astrophysics, Earth and Planetary Astrophysics, astro-ph.EP</xsl:when>
					<xsl:when test="$subject = 'astro-ph.GA'"> Astrophysics, Galaxy Astrophysics, astro-ph.GA</xsl:when>
					<xsl:when test="$subject = 'astro-ph.HE'"> Astrophysics, High Energy Astrophysical Phenomena, astro-ph.HE</xsl:when>
					<xsl:when test="$subject = 'astro-ph.IM'"> Astrophysics, Instrumentation and Methods for Astrophysics, astro-ph.IM</xsl:when>
					<xsl:when test="$subject = 'astro-ph.SR'"> Astrophysics, Solar and Stellar Astrophysics, astro-ph.SR</xsl:when>
					<xsl:when test="$subject = 'cond-mat'"> Condensed Matter, cond-mat</xsl:when>
					<xsl:when test="$subject = 'cond-mat.dis-nn'"> Condensed Matter, Disordered Systems and Neural Networks, cond-mat.dis-nn</xsl:when>
					<xsl:when test="$subject = 'cond-mat.mtrl-sci'"> Condensed Matter, Materials Science, cond-mat.mtrl-sci</xsl:when>
					<xsl:when test="$subject = 'cond-mat.mes-hall'"> Condensed Matter, Mesoscale and Nanoscale Physics, cond-mat.mes-hall</xsl:when>
					<xsl:when test="$subject = 'cond-mat.other'"> Condensed Matter, cond-mat.other</xsl:when>
					<xsl:when test="$subject = 'cond-mat.quant-gas'"> Condensed Matter, Quantum Gases, cond-mat.quant-gas</xsl:when>
					<xsl:when test="$subject = 'cond-mat.soft'"> Condensed Matter, Soft Condensed Matter, cond-mat.soft</xsl:when>
					<xsl:when test="$subject = 'cond-mat.stat-mech'"> Condensed Matter, Statistical Mechanics, cond-mat.stat-mech</xsl:when>
					<xsl:when test="$subject = 'cond-mat.str-el'"> Condensed Matter, Strongly Correlated Electrons, cond-mat.str-el</xsl:when>
					<xsl:when test="$subject = 'cond-mat.supr-con'"> Condensed Matter, Superconductivity, cond-mat.supr-con</xsl:when>
					<xsl:when test="$subject = 'physics'"> Physics, physics</xsl:when>
					<xsl:when test="$subject = 'physics.acc-ph'"> Physics, Accelerator Physics, physics.acc-ph</xsl:when>
					<xsl:when test="$subject = 'physics.ao-ph'"> Physics, Atmospheric and Oceanic Physics, physics.ao-ph</xsl:when>
					<xsl:when test="$subject = 'physics.atom-ph'"> Physics, Atomic Physics, physics.atom-ph</xsl:when>
					<xsl:when test="$subject = 'physics.atm-clus'"> Physics, Atomic and Molecular Clusters, physics.atm-clus</xsl:when>
					<xsl:when test="$subject = 'physics.bio-ph'">  Physics, Biological Physics, physics.bio-ph</xsl:when>
					<xsl:when test="$subject = 'physics.chem-ph'"> Physics, Chemical Physics, physics.chem-ph</xsl:when>
					<xsl:when test="$subject = 'physics.class-ph'"> Physics, Classical Physics, physics.class-ph</xsl:when>
					<xsl:when test="$subject = 'physics.comp-ph'">  Physics, Computational Physics, physics.comp-ph</xsl:when>
					<xsl:when test="$subject = 'physics.data-an'">  Physics, Data Analysis, Statistics and Probability, physics.data-an</xsl:when>
					<xsl:when test="$subject = 'physics.flu-dyn'"> Physics, Fluid Dynamics, physics.flu-dyn</xsl:when>
					<xsl:when test="$subject = 'physics.gen-ph'"> Physics, General Physics, physics.gen-ph</xsl:when>
					<xsl:when test="$subject = 'physics.geo-ph'"> Physics, Geophysics, physics.geo-ph</xsl:when>
					<xsl:when test="$subject = 'physics.hist-ph'"> Physics, History of Physics, physics.hist-ph</xsl:when>
					<xsl:when test="$subject = 'physics.ins-det'"> Physics, Instrumentation and Detectors, physics.ins-det</xsl:when>
					<xsl:when test="$subject = 'physics.med-ph'"> Physics, Medical Physics, physics.med-ph</xsl:when>
					<xsl:when test="$subject = 'physics.optics'"> Physics, Optics, physics.optics</xsl:when>
					<xsl:when test="$subject = 'physics.ed-ph'"> Physics, Physics Education, physics.ed-ph</xsl:when>
					<xsl:when test="$subject = 'physics.soc-ph'"> Physics, Physics and Society, physics.soc-ph</xsl:when>
					<xsl:when test="$subject = 'physics.plasm-ph'">	Physics, Plasma Physics, physics.plasm-ph</xsl:when>
					<xsl:when test="$subject = 'physics.pop-ph'"> Physics, Popular Physics, physics.pop-ph</xsl:when>
					<xsl:when test="$subject = 'physics.space-ph'"> Physics, Space Physics, physics.space-ph</xsl:when>
					<xsl:when test="$subject = 'gr-qc'">General Relativity and Quantum Cosmology, gr-qc</xsl:when>
					<xsl:when test="$subject = 'hep-ex'">High Energy Physics - Experiment, hep-ex</xsl:when>
					<xsl:when test="$subject = 'hep-lat'">High Energy Physics - Lattice, hep-lat</xsl:when>
					<xsl:when test="$subject = 'hep-ph'">High Energy Physics - Phenomenology, hep-ph</xsl:when>
					<xsl:when test="$subject = 'hep-th'">High Energy Physics - Theory, hep-th</xsl:when>
					<xsl:when test="$subject = 'math-ph'">Mathematical Physics, math-ph</xsl:when>
					<xsl:when test="$subject = 'nucl-ex'">Nuclear Experiment, nucl-ex</xsl:when>
					<xsl:when test="$subject = 'nucl-th'">Nuclear Theory, nucl-th</xsl:when>
					<xsl:when test="$subject = 'quant-ph'">Quantum Physics, quant-ph</xsl:when>
					<xsl:when test="$subject = 'math'">Mathematics, math</xsl:when>
					<xsl:when test="$subject = 'math.AG'">Mathematics, Algebraic Geometry, math.AG</xsl:when>
					<xsl:when test="$subject = 'math.AT'">Mathematics, Algebraic Topology, math.AT</xsl:when>
					<xsl:when test="$subject = 'math.AP'">Mathematics, Analysis of PDEs, math.AP</xsl:when>
					<xsl:when test="$subject = 'math.CT'">Mathematics, Category Theory, math.CT</xsl:when>
					<xsl:when test="$subject = 'math.CA'">Mathematics, Classical Analysis and ODEs, math.CA</xsl:when>
					<xsl:when test="$subject = 'math.CO'">Mathematics, Combinatorics, math.CO</xsl:when>
					<xsl:when test="$subject = 'math.AC'">Mathematics, Commutative Algebra, math.AC</xsl:when>
					<xsl:when test="$subject = 'math.CV'">Mathematics, Complex Variables, math.CV</xsl:when>
					<xsl:when test="$subject = 'math.DG'">Mathematics, Differential Geometry, math.DG</xsl:when>
					<xsl:when test="$subject = 'math.DS'">Mathematics, Dynamical Systems, math.DS</xsl:when>
					<xsl:when test="$subject = 'math.FA'">Mathematics, Functional Analysis, math.FA</xsl:when>
					<xsl:when test="$subject = 'math.GM'">Mathematics, General Mathematics, math.GM</xsl:when>
					<xsl:when test="$subject = 'math.GN'">Mathematics, General Topology, math.GN</xsl:when>
					<xsl:when test="$subject = 'math.GT'">Mathematics, Geometric Topology, math.GT</xsl:when>
					<xsl:when test="$subject = 'math.GR'">Mathematics, Group Theory, math.GR</xsl:when>
					<xsl:when test="$subject = 'math.HO'">Mathematics, History and Overview, math.HO</xsl:when>
					<xsl:when test="$subject = 'math.IT'">Mathematics, Information Theory, math.IT</xsl:when>
					<xsl:when test="$subject = 'math.KT'">Mathematics, K-Theory and Homology, math.KT</xsl:when>
					<xsl:when test="$subject = 'math.LO'">Mathematics, Logic, math.LO</xsl:when>
					<xsl:when test="$subject = 'math.MP'">Mathematics, Mathematical Physics, math.MP</xsl:when>
					<xsl:when test="$subject = 'math.MG'">Mathematics, Metric Geometry, math.MG</xsl:when>
					<xsl:when test="$subject = 'math.NT'">Mathematics, Number Theory, math.NT</xsl:when>
					<xsl:when test="$subject = 'math.NA'">Mathematics, Numerical Analysis, math.NA</xsl:when>
					<xsl:when test="$subject = 'math.OA'">Mathematics, Operator Algebras, math.OA</xsl:when>
					<xsl:when test="$subject = 'math.OC'">Mathematics, Optimization and Control, math.OC</xsl:when>
					<xsl:when test="$subject = 'math.PR'">Mathematics, Probability, math.PR</xsl:when>
					<xsl:when test="$subject = 'math.QA'">Mathematics, Quantum Algebra, math.QA</xsl:when>
					<xsl:when test="$subject = 'math.RT'">Mathematics, Representation Theory, math.RT</xsl:when>
					<xsl:when test="$subject = 'math.RA'">Mathematics, Rings and Algebras, math.RA</xsl:when>
					<xsl:when test="$subject = 'math.SP'">Mathematics, Spectral Theory, math.SP</xsl:when>
					<xsl:when test="$subject = 'math.ST'">Mathematics, Statistics, math.ST</xsl:when>
					<xsl:when test="$subject = 'math.SG'">Mathematics, Symplectic Geometry, math.SG</xsl:when>
					<xsl:when test="$subject = 'nlin'">Nonlinear Sciences, nlin</xsl:when>
					<xsl:when test="$subject = 'nlin.AO'">Nonlinear Sciences, Adaptation and Self-Organizing Systems, nlin.AO</xsl:when>
					<xsl:when test="$subject = 'nlin.CG'">Nonlinear Sciences, Cellular Automata and Lattice Gases, nlin.CG</xsl:when>
					<xsl:when test="$subject = 'nlin.CD'">Nonlinear Sciences, Chaotic Dynamics, nlin.CD</xsl:when>
					<xsl:when test="$subject = 'nlin.SI'">Nonlinear Sciences, Exactly Solvable and Integrable Systems, nlin.SI</xsl:when>
					<xsl:when test="$subject = 'nlin.PS'">Nonlinear Sciences, Pattern Formation and Solitons, nlin.PS</xsl:when>
					<xsl:when test="$subject = 'cs'">Computer Science, cs</xsl:when>
					<xsl:when test="$subject = 'cs.AR'">Computer Science, Architecture, cs.AR</xsl:when>
					<xsl:when test="$subject = 'cs.AI'">Computer Science, Artificial Intelligence, cs.AI</xsl:when>
					<xsl:when test="$subject = 'cs.CL'">Computer Science, Computation and Language, cs.CL</xsl:when>
					<xsl:when test="$subject = 'cs.CC'">Computer Science, Computational Complexity, cs.CC</xsl:when>
					<xsl:when test="$subject = 'cs.CE'">Computer Science, Computational Engineering, Finance, and Science, cs.CE</xsl:when>
					<xsl:when test="$subject = 'cs.CG'">Computer Science, Computational Geometry, cs.CG</xsl:when>
					<xsl:when test="$subject = 'cs.GT'">Computer Science, Computer Science and Game Theory, cs.GT</xsl:when>
					<xsl:when test="$subject = 'cs.CV'">Computer Science, Computer Vision and Pattern Recognition, cs.CV</xsl:when>
					<xsl:when test="$subject = 'cs.CY'">Computer Science, Computers and Society, cs.CY</xsl:when>
					<xsl:when test="$subject = 'cs.CR'">Computer Science, Cryptography and Security, cs.CR</xsl:when>
					<xsl:when test="$subject = 'cs.DS'">Computer Science, Data Structures and Algorithms, cs.DS</xsl:when>
					<xsl:when test="$subject = 'cs.DB'">Computer Science, Databases, cs.DB</xsl:when>
					<xsl:when test="$subject = 'cs.DL'">Computer Science, Digital Libraries, cs.DL</xsl:when>
					<xsl:when test="$subject = 'cs.DM'">Computer Science, Discrete Mathematics, cs.DM</xsl:when>
					<xsl:when test="$subject = 'cs.DC'">Computer Science, Distributed, Parallel, and Cluster Computing, cs.DC</xsl:when>
					<xsl:when test="$subject = 'cs.FL'">Computer Science, Formal Languages and Automata Theory, cs.FL</xsl:when>
					<xsl:when test="$subject = 'cs.GL'">Computer Science, General Literature, cs.GL</xsl:when>
					<xsl:when test="$subject = 'cs.GR'">Computer Science, Graphics, cs.GR</xsl:when>
					<xsl:when test="$subject = 'cs.HC'">Computer Science, Human-Computer Interaction, cs.HC</xsl:when>
					<xsl:when test="$subject = 'cs.IR'">Computer Science, Information Retrieval, cs.IR</xsl:when>
					<xsl:when test="$subject = 'cs.IT'">Computer Science, Information Theory, cs.IT</xsl:when>
					<xsl:when test="$subject = 'cs.LG'">Computer Science, Learning, cs.LG</xsl:when>
					<xsl:when test="$subject = 'cs.LO'">Computer Science, Logic in Computer Science, cs.LO</xsl:when>
					<xsl:when test="$subject = 'cs.MS'">Computer Science, Mathematical Software, cs.MS</xsl:when>
					<xsl:when test="$subject = 'cs.MA'">Computer Science, Multiagent Systems, cs.MA</xsl:when>
					<xsl:when test="$subject = 'cs.MM'">Computer Science, Multimedia, cs.MM</xsl:when>
					<xsl:when test="$subject = 'cs.NI'">Computer Science, Networking and Internet Architecture, cs.NI</xsl:when>
					<xsl:when test="$subject = 'cs.NE'">Computer Science, Neural and Evolutionary Computing, cs.NE</xsl:when>
					<xsl:when test="$subject = 'cs.NA'">Computer Science, Numerical Analysis, cs.NA</xsl:when>
					<xsl:when test="$subject = 'cs.OS'">Computer Science, Operating Systems, cs.OS</xsl:when>
					<xsl:when test="$subject = 'cs.OH'">Computer Science, Computer Science, cs.OH</xsl:when>
					<xsl:when test="$subject = 'cs.PF'">Computer Science, Performance, cs.PF</xsl:when>
					<xsl:when test="$subject = 'cs.PL'">Computer Science, Programming Languages, cs.PL</xsl:when>
					<xsl:when test="$subject = 'cs.RO'">Computer Science, Robotics, cs.RO</xsl:when>
					<xsl:when test="$subject = 'cs.SE'">Computer Science, Software Engineering, cs.SE</xsl:when>
					<xsl:when test="$subject = 'cs.SD'">Computer Science, Sound, cs.SD</xsl:when>
					<xsl:when test="$subject = 'cs.SC'">Computer Science, Symbolic Computation, cs.SC</xsl:when>
					<xsl:when test="$subject = 'q-bio'">Quantitative Biology, q-bio</xsl:when>
					<xsl:when test="$subject = 'q-bio.BM'">Quantitative Biology, Biomolecules, q-bio.BM</xsl:when>
					<xsl:when test="$subject = 'q-bio.CB'">Quantitative Biology, Cell Behavior, q-bio.CB</xsl:when>
					<xsl:when test="$subject = 'q-bio.GN'">Quantitative Biology, Genomics, q-bio.GN</xsl:when>
					<xsl:when test="$subject = 'q-bio.MN'">Quantitative Biology, Molecular Networks, q-bio.MN</xsl:when>
					<xsl:when test="$subject = 'q-bio.NC'">Quantitative Biology, Neurons and Cognition, q-bio.NC</xsl:when>
					<xsl:when test="$subject = 'q-bio.OT'">Quantitative Biology, Quantitative Biology, q-bio.OT</xsl:when>
					<xsl:when test="$subject = 'q-bio.PE'">Quantitative Biology, Populations and Evolution, q-bio.PE</xsl:when>
					<xsl:when test="$subject = 'q-bio.QM'">Quantitative Biology, Quantitative Methods, q-bio.QM</xsl:when>
					<xsl:when test="$subject = 'q-bio.SC'">Quantitative Biology, Subcellular Processes, q-bio.SC</xsl:when>
					<xsl:when test="$subject = 'q-bio.TO'">Quantitative Biology, Tissues and Organs, q-bio.TO</xsl:when>
					<xsl:when test="$subject = 'q-fin'">Quantitative Finance, q-fin</xsl:when>
					<xsl:when test="$subject = 'q-fin.CP'">Quantitative Finance, Computational Finance, q-fin.CP</xsl:when>
					<xsl:when test="$subject = 'q-fin.GN'">Quantitative Finance, General Finance, q-fin.GN</xsl:when>
					<xsl:when test="$subject = 'q-fin.PM'">Quantitative Finance, Portfolio Management, q-fin.PM</xsl:when>
					<xsl:when test="$subject = 'q-fin.PR'">Quantitative Finance, Pricing of Securities, q-fin.PR</xsl:when>
					<xsl:when test="$subject = 'q-fin.RM'">Quantitative Finance, Risk Management, q-fin.RM</xsl:when>
					<xsl:when test="$subject = 'q-fin.ST'">Quantitative Finance, Statistical Finance, q-fin.ST</xsl:when>
					<xsl:when test="$subject = 'q-fin.TR'">Quantitative Finance, Trading and Market Microstructure, q-fin.TR</xsl:when>
					<xsl:when test="$subject = 'stat'">Statistics, stat</xsl:when>
					<xsl:when test="$subject = 'stat.AP'">Statistics, Applications, stat.AP</xsl:when>
					<xsl:when test="$subject = 'stat.CO'">Statistics, Computation, stat.CO</xsl:when>
					<xsl:when test="$subject = 'stat.ML'">Statistics, Machine Learning, stat.ML</xsl:when>
					<xsl:when test="$subject = 'stat.ME'">Statistics, Methodology, stat.ME</xsl:when>
					<xsl:when test="$subject = 'stat.TH'">Statistics, Statistics Theory, stat.TH</xsl:when>		
					
					<xsl:otherwise>
						<xsl:value-of select="$subject"/>
					</xsl:otherwise>
								
		</xsl:choose>
	</xsl:template>
</xsl:stylesheet>