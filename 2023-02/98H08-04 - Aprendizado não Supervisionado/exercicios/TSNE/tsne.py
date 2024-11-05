
import warnings
import numpy as np
import pandas as pd
import seaborn as sns
import matplotlib.pyplot as plt
from sklearn.manifold import TSNE
from sklearn.preprocessing import StandardScaler, LabelEncoder
from sklearn.datasets import fetch_openml
from sklearn.metrics import silhouette_score
from sklearn.cluster import KMeans

#remove warnings
warnings.filterwarnings('ignore')


def tsne_silhouette_score(tsne_results, n_clusters, random_state=42):
    """
    Computes the silhouette score for t-SNE results.
    
    Parameters:
    - tsne_results: 2D array or DataFrame containing t-SNE results.
    - n_clusters: Number of clusters expected in the data (this is needed for silhouette score).
    
    Returns:
    - Silhouette score.
    """
    
    # Using KMeans to get cluster labels for t-SNE results
    kmeans = KMeans(n_clusters=n_clusters, random_state=random_state).fit(tsne_results)
    cluster_labels = kmeans.labels_
    
    # Compute silhouette score
    silhouette_avg = silhouette_score(tsne_results, cluster_labels)
    
    return silhouette_avg



def plot_tsne_results(tsne_results, n_components, state, labels=None):
    """
    Plots the 2D t-SNE results.
    
    Parameters:
    - tsne_results: A 2D array or DataFrame containing the t-SNE results.
    - n_components: Number of components used in the t-SNE algorithm.
    - state: Random state used in the t-SNE algorithm.
    - labels: Labels for each data point for coloring. If None, all points are colored with the same color.
    """
    
    # Extracting the two dimensions from the results
    x = tsne_results[:, 0]
    y = tsne_results[:, 1]
    
    plt.figure(figsize=(8, 6))
    
    # If labels are provided, plot using different colors for each label
    if labels is not None:
        unique_labels = list(set(labels))
        for label in unique_labels:
            idx = labels == label
            plt.scatter(x[idx], y[idx], label=label, alpha=0.7)
        plt.legend()
    else:
        plt.scatter(x, y, alpha=0.7)
    
    plt.xlabel('t-SNE Dimension 1')
    plt.ylabel('t-SNE Dimension 2')
    plt.title(f't-SNE {n_components} Components - Random State {state}')
    plt.grid(True)
    plt.show()

