
import pandas as pd

token_hf = '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'
path='D:/Disco/Data/huggingface/'
reviews_path = 'D:/Disco/Data/datasets/amazon_us_reviews/'


class Utils:
    def __init__(self) -> None:
        self.reviews = []

    def load_reviews(self, file_name):
        self.reviews = pd.read_parquet(reviews_path + file_name)

    def get_reviews_by_product_and_category(self, product, category):
        return self.reviews[(self.reviews['product_category'] == category) & (self.reviews['product_title'] == product)]

    def get_prompt(self, product):
        return f"Please analyze the provided reviews of the product '{product}'. \n  \
        Create a concise summary that encapsulates the key opinions and sentiments expressed in these reviews. \n \
        The summary should be structured as if it's a single comprehensive review of the product. \n \
        The summary should mimic the style and tone of a customer reviews, making it relatable and genuine. \n \
        Also, provide a list of 5 tags that represent what the customers are saying about the product, give the balance between positive and negative aspects about the product, the tags have this format: #TagName. \
        Format your response as follows: \n \
            Product: {product}\n \
            Summary: [Your summary here]\n \
            Tags: #tag1 #tag2 #tag3 #tag4 #tag5\n \
        List of reviews: \n"