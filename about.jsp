<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<jsp:useBean id='header' class='servlets.beans.HeaderBean' />
<html>
<% 
   header.setHeaderType(servlets.beans.HeaderBean.HeaderType.GCD);
    header.printStdHeader(out,"About GCD", request.getRemoteUser()!=null);
%>                    
        
        <p>	
        <table><td  width=950, align="justify">
        <ol>
                <li type=I><b>General Scope of this Database</b></li><p>

                        <dl>
                                <dd>
                                The Genome Cluster Database (GCD) is an integrated mining tool for the genome-wide family and singlet proteins from <i>Arabidopsis thaliana</i> and <i>Oryza sativa</i> spp. <i>japonica</i>. Their proteomes have been clustered here into families by employing two independent approaches. The program BLASTCLUST was used for similarity-based clustering (<a href="#BCL">BCL</a>) and hmmpfam searches were used for domain-based clustering (<a href="#BCL">HCL</a>). Since the two methods have reciprocal advantages and disadvantages, both cluster sets have been integrated into GCD with efficient tools to mine them together. Additional well annotated genomes may be included into this clustering pipeline in the future. The developed GCD interface provides user-friendly query and visualization functions for intra- and inter-species protein family comparisons, and for retrieval of sequences, multiple alignments, phylogenetic trees and information about putative orthologs from other kingdoms.<p>

                                </dd>
                        </dl>
                        <p>
                        <table>
                                 <tr>
                                         <td align="center", width=1000><img src="images/web.jpg"></td>
                                </tr>
                                <tr>
                                        <td align="center", width=1000>Data Flow in GCD</td>

                                </tr>
                        </table>
                <p>
                <a name="limitations"></a>
                <li type=I><b>Limitations of Data Sets</b></li><p>

                        <dl>
                                <dt><b>Cluster, Alignment and Tree Data</b></dt>
                                <dd>
                                Accurate clustering of entire proteomes is a complex task. Currently, GCD provides data of high quality for most but not all families. Due to this limitation, clusters and trees in GCD should only be used after quality inspection of the corresponding alignments using the available consensus and domain shading tools.<p>				    </dd>
                        </dl>
                <p>

		<li type=I><b>Search Functions</b></li><p>
                        <dl>
                                <a name="search"></a>
                                <dt><b><a href="index.jsp">Basic Searches in Single or Batch Mode</a></b></dt>
                                <dd>

                                Database searches can be performed against the following five field categories:					
                                        <ol>
                                                <li>Functional descriptions (<i>e.g.</i> '<a href="index.jsp?fieldName=Description&limit=0&input=desaturase%20AND%20fatty%20acid">desaturase AND fatty acid</a>'). Boolean query connectors can be included here: 'AND' &nbsp; 'OR' &nbsp; 'NOT'. </li>
                                                <li>Cluster names (<i>e.g.</i> '<a href="index.jsp?fieldName=Cluster%20Name&limit=0&input=oxidoreductase%20activity">oxidoreductase activity</a>)'</li>

                                                <li>One or many locus IDs from Arabidopsis or rice (<i>e.g.</i> '<a href="index.jsp?fieldName=Id%20Id&limit=0&input=At1g01190%20At3g62720%209631.m01858">At1g01190 &nbsp; At3g62720 &nbsp; 9631.m01858</a>')</li> 
                                                <li>Cluster or Pfam ID numbers (<i>e.g.</i> '<a href="index.jsp?fieldName=Cluster%20Id&limit=0&input=53">53</a>' or '<a href="http://bioweb.ucr.edu:8180/databaseWeb/index.jsp?fieldName=Cluster%20Id&limit=0&input=PF00067">PF00067</a>')</li>

                                                <li>Gene Ontology keys (<i>e.g.</i> '<a href="index.jsp?fieldName=GO%20Number&limit=0&input=GO:0019825">GO:0019825</a>')</li>
                                        </ol>
                                Before submitting a query, the correct <b>search category</b> needs to be selected in the drop down menu on the bottom of the search page. The maximum number of query hits can be specified in a separate field. In addition, all searches can be delimited against one organism by selecting/de-selecting one of them. A user-friendly 'Loop Query' system on the resulting <a href="http://bioweb.ucr.edu:8180/databaseWeb/QueryPageServlet?searchType=Id&displayType=seqView&inputKey=At1g18690">List Page</a> allows quick retrieval of all members of a family of interest by clicking on the organism distribution links (<i>e.g.</i> '<a href="http://bioweb.ucr.edu:8180/databaseWeb/index.jsp?fieldName=Cluster%20Id&limit=0&input=PF05637">7 Ath &nbsp; 8 Osa</a>'). A similar facility is in place to quickly retrieve all proteins containing a Pfam domain of interest by clicking on its link under domain cluster ID. This action will loop through the Advanced Search Page.

                                </dd>

                                <p>
                                <dt><b><a href="advancedSearch.jsp">Advanced Searches</a></b></dt>
                                <dd>
                                Combinatorial queries of expandable complexity can be constructed on the Advanced Search Page. Several predefined queries are available here to retrieve organism-restricted clusters within certain size intervalls.	
                                </dd>
                                <p>
                                <dt><b><a href="http://bioweb.ucr.edu/scripts/clusterSummary.pl?sort_col=Size">Cluster Table Search</a></b></dt>
                                <dd>

                                A search- and sortable cluster table enables family mining by cluster sizes, cluster names and family IDs. The cluster method used for generating a cluster is defined in the table by the type of the 'Family ID' number. Clusters that were generated with BLASTCLUST (<a href="#BCL">BCL</a>) have blank numbers, while domain-based clusters (<a href="#BCL">HCL</a>) follow the Pfam ID syntax, <i>e.g.</i>: 'PF00026'.    
                                </dd>

                        </dl>
                <p>
                <a name="ResultList"></a>
                <li type=I><b>Result List Pages</b></li><p>

                        <dl>
                                <dt><b>General</b></dt>
                                <dd>
                                All of the above query types return a Result List page that provides the <i>A. thaliana</i> members on the top and the <i>O. sativa</i> members on the bottom. The cluster association of the entries is provided by their cluster identifiers (ID) and the cluster sizes are specified by the cluster size links, <i>e.g.</i>: '<u>7 Ath 15 Osa</u>' stands for a cluster with 7 <i>A. thaliana</i> and 15 <i>O. sativa</i> members. The result statistics on the beginning of the page lists the number of loci, gene models and clusters returned by a query. To restrict a query to a protein family of interest, users can simply click on the organism distribution links (<i>e.g.</i> '<a href="http://bioweb.ucr.edu:8180/databaseWeb/index.jsp?fieldName=Cluster%20Id&limit=0&input=PF05637">7 Ath &nbsp; 8 Osa</a>'). This actions sends the correct query syntax back to the main page which returns the requested entries upon submission.  
                                </dd>

                                <p>
                                <dt><b>Sequence Retrieval</b></dt>
                                <dd>
                                The following sequence types can be retrieved in batches for any query result by selecting their check-boxes: proteins, CDSs, transcriptional models, UTRs, intergenic and putative promoter regions. The default sequence view is HTML format. Text based retrieval in FASTA format can be activated by making the corresponding selection in the adjacent drop-down menu.  
                                </dd>
                                <p>
                                <dt><b>Alignments and Trees</b></dt>
                                <dd>

                                Multiple alignments and trees are currently available for all <a href="#BCL">BCL_35%</a> clusters and the <a href="#BCL">HCL</a> clusters. If requested by users, they can also be provided for the less sensitive BCL clustering using cutoffs of 50% and 70% sequence identity. The consensus shading view highlights conserved residues and the domain shading view identifies the Pfam domains in all members of a cluster. The domain viewer can be extremely useful for evaluating the quality of an alignment and localizing the functional regions in the context of a multiple alignment. All alignments are generated with a local installation of the <a href="http://prodes.toulouse.inra.fr/multalin/multali">MultAlin</a> program. The obtained alignments for each family are used to calculate phylogenetic trees with the <a href="http://evolution.genetics.washington.edu/phylip.html">PHYLIP</a> package. A distance-based neighbor-joining method has been chosen for this step that uses PROTDIST with the 'categories model' setting for generating distance matrices, NEIGHBOR for tree construction and the midpoint method in RETREE for defining root positions.	
                                </dd>
                                <p>

                                <dt><b>GO-Based Family Naming</b></dt>
                                <dd>
                                Functional names were assigned to clusters with an automated strategy that is based on the available Gene Ontology (GO) annotations from TIGR. The approach uses the deepest and most common Molecular Function GO term for assigning a single name to a family. Typically, this method provides useful names for many but not all families. The results will improve over time with updates in the available GO annoations of the two organisms.  
                                </dd>
                                <p>
                                <dt><b>Online Batch Viewing and Analysis Tools</b></dt>
                                <dd>
                                The link bar on top of the Result List Pages provides access to several online batch viewing and analysis tools. A Gene Structure viewer displays the UTR-Exon-Intron structure of all retrieved <i>A.thalina</i> and <i>O. sativa</i> entries on one page. A chromosome mapping tool visualizes the localization on their chromosomes. Gene Ontology pie charts for the Molecular Function category can also be displayed for both organisms. An online <a href="http://hmmer.wustl.edu/">hmmalign</a> service allows users to generate multiple alignments for any sequence set of interest against a chosen Pfam domain.  	
                                </dd>

                        </dl>
                <p>
                <li type=I><b>Sequences in GCD</b></li><p>
                        <dl>
                                <dd>
                                        <ol>
                                                <li type=i>TIGR protein sequences used for family clustering</li> 
                                                <ul>

                                                        <li><i>A. thaliana</i>: <a href="ftp://ftp.tigr.org/pub/data/a_thaliana/ath1/SEQUENCES">ATH1.pep</a></li>
                                                        <li><i>O. sativa spp. japonica</i>: <a href="ftp://ftp.tigr.org/pub/data/Eukaryotic_Projects/o_sativa/annotation_dbs/pseudomolecules/version_3.0/all_chrs/">all.pep</a></li>
                                                </ul>
                                                        <li type=i>TIGR pseudochromosomes for feature download and viewing</li> 
                                                <ul>
                                                        <li><i>A. thaliana</i>: <a href="ftp://ftp.tigr.org/pub/data/a_thaliana/ath1/PSEUDOCHROMOSOMES">*.xml</a></li>

                                                        <li><i>O. sativa spp. japonica</i>: <a href="ftp://ftp.tigr.org/pub/data/Eukaryotic_Projects/o_sativa/annotation_dbs/pseudomolecules/version_3.0/">*.xml</a></li>
                                                </ul>
                                                        <li type=i>Ortholog identification in other kingdoms</li> 
                                                <ul>
                                                <li><a href="http://www.pir.uniprot.org/database/download.shtml">UniProt</a> (indirect hyperlink access through GCD)</li>
                                                </ul>

                                        </ol>
                                </dd>
                        </dl>
                <p>
                <li type=I><b>Proteome Clustering</b></li><p>
                        <dl>
                                <dd>
                                The protein sequences in GCD are clustered by two independent approaches: 
                                <ol>

                                        <a name="BCL"></a>
                                        <li type=A>BLAST-based Similarity Clustering (BCL)</li>
                                        The <a href="ftp://ftp.ncbi.nlm.nihi.gov/blast/executables">BLASTCLUST</a> program from NCBI was used to cluster the proteins by sequence similarity. Threshold parameters of 50% overlap and 35% sequence identity were used for high-sensitivity clustering (BCL_35%). Most resources for comparing the different approaches (<i>e.g.</i> <a href="http://bioinfo.ucr.edu/cgi-bin/clusterSummary.pl?sort_col=Size">Table</a> & <a href="http://bioinfo.ucr.edu/cgi-bin/clusterStats.pl">Stats</a> pages) were performed with this BCL_35% cluster set. Two less stringent cluster sets with 50% and 70% identity were generated for identifying sub-clusters on the Result List pages (BCL_50%, BCL70%). Prior to the similarity clustering, low-compexity regions of the proteins were masked with the <a href="http://maine.ebi.ac.uk:8000/services/cast/">CAST</a> program.
                                        <a name="HCL"></a>

                                        <li type=A>HMM Domain Arrangement Clustering (HCL)</li>
                                        To cluster the proteins based on their domain signatures, their Pfam domains were identified with <a href="http://hmmer.wustl.edu/">hmmpfam</a> searches against the latest <a href="http://www.sanger.ac.uk/Software/Pfam/ftp.shtml">Pfam_ls</a> HMM library. Subsequently, the proteins were clustered with a custom Perl script based on their order of identified domains using an HMM e-value of &le;0.1 as cutoff.
                                </ol>
                                </dd>
                        </dl>

                <p>
                <a name="Stats"></a>
                <li type=I><b>Cluster Statistics Page</b></li><p>
                        <dl>
                                <dd>
                                A <a href="http://bioweb.ucr.edu/scripts/clusterStats.pl">Cluster Statistics Page</a> has been implemented to summarize and track the cluster results of the two species. It provides the size and number of singlet and family proteins for the two clustering methods.   
                                </dd>

                        </dl>
                <p>	
                <li type=I><b>Automated Update Strategy</b></li><p>
                        <dl>
                                <dd>
                                All data upload and clustering steps have been automated in GCD with Perl scripts to allow rapid updates when new versions of the genome annotations or Pfam domain database are released in the future. Changes in the results will be tracked on the above Cluster Statistics Page.    
                                </dd>
                        </dl>
                <p>	

        </ol>
        </table>	
    <% header.printFooter(); %>
</html>
