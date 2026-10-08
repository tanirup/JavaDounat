# JavaDounat

JavaDounatプロジェクトのチーム開発用Git / GitHub運用ルールです。

このREADMEでは、

- 最初に行う設定
- 毎回の作業手順
- ブランチの使い方
- Eclipseからの操作方法
- GitHubへのPush
- Pull Request
- エラーが出た場合の対処
- チーム内ルール

についてまとめています。

---

# チームメンバーが最初にすること

## 1. プロジェクトをクローンする

最初にGitHubのリポジトリ画面を開き、

`Code` → `HTTPS`

からURLをコピーします。

JavaDounatのリポジトリ：

```text
https://github.com/tanirup/JavaDounat.git
```

### ターミナルを使う場合

```bash
git clone https://github.com/tanirup/JavaDounat.git
cd JavaDounat
```

これで、自分のPCにJavaDounatプロジェクトがコピーされます。

### Eclipseを使う場合

Eclipseで次の順番に操作します。

```text
File
↓
Import
↓
Git
↓
Projects from Git
↓
Clone URI
```

GitHubからコピーしたURLを入力してクローンします。

---

## 2. Gitのユーザー情報を設定する

このPCで初めてGitを使用する場合だけ設定します。

```bash
git config --global user.name "自分の名前またはGitHubユーザー名"
git config --global user.email "GitHubに登録したメールアドレス"
```

設定内容を確認する場合：

```bash
git config --global --list
```

すでに設定済みの場合は、もう一度設定する必要はありません。

> **重要**
>
> `git config`はGitHubへのログイン設定ではありません。
>
> 「誰がコミットしたか」を記録するための設定です。
>
> クローン後でも問題ありませんが、最初のコミットをする前には設定してください。

---

# GitHubへの認証について

HTTPSでGitHubへPushするとき、GitHubの通常のパスワードは使用しません。

Personal Access Tokenを使用します。

Eclipseなどでログインを求められた場合：

```text
User
→ GitHubのユーザー名

Password
→ Personal Access Token
```

Personal Access TokenはGitHubの

```text
Settings
↓
Developer settings
↓
Personal access tokens
↓
Fine-grained tokens
```

から作成できます。

JavaDounatへのPushだけなら、基本的に次の権限で問題ありません。

```text
Repository access
→ JavaDounatを選択

Repository permissions
→ Contents
→ Read and write
```

> **注意**
>
> Personal Access Tokenはパスワードと同じ重要な情報です。
>
> GitHub、README、Discord、LINE、スクリーンショットなどに絶対に公開しないでください。

---

# 毎回の作業手順

基本的な作業の流れは次の通りです。

```text
mainを最新にする
↓
作業用ブランチを作る
↓
コードを編集
↓
コミット
↓
Push
↓
Pull Request
↓
レビュー
↓
mainへマージ
```

---

## 1. 作業前にmainを最新状態にする

新しい作業を始める前に、必ず`main`へ移動します。

```bash
git switch main
```

その後、GitHub上の最新状態を取り込みます。

```bash
git pull origin main
```

---

## 2. 自分の作業用ブランチを作る

`main`を直接編集せず、自分の作業用ブランチを作ります。

ブランチとは、mainに直接影響を与えずに作業するための「作業場所」です。

```bash
git switch -c feature-作業名
```

例えば、ログイン画面を作る場合：

```bash
git switch -c feature-login
```

このコマンドは、

```text
feature-loginというブランチを作成
↓
feature-loginへ移動
```

という意味です。

### 現在のブランチを確認する

```bash
git branch
```

例：

```text
  main
* feature-login
```

`*`が付いているブランチが、現在作業中のブランチです。

### ブランチ名の例

```text
ログイン画面
feature-login

投稿機能
feature-post

メインページ
feature-mainpage

注文機能
feature-order

デザイン修正
fix-design
```

> **重要**
>
> 新しい機能を作るときは、古いfeatureブランチから新しいfeatureブランチを作らないでください。
>
> 必ず次の順番で作業します。

```bash
git switch main
git pull origin main
git switch -c feature-作業名
```

つまり、

```text
mainへ戻る
↓
mainを最新にする
↓
新しいブランチを作る
```

という流れです。

---

## 3. コードを編集する

自分の担当する機能を編集します。

変更状況を確認する場合：

```bash
git status
```

変更内容を詳しく確認する場合：

```bash
git diff
```

---

## 4. 変更をコミットする

変更したファイルをGitに追加します。

```bash
git add .
```

その後、コミットします。

```bash
git commit -m "変更内容を書く"
```

例：

```bash
git commit -m "ログイン画面を追加"
```

```bash
git commit -m "MainPageを作成"
```

```bash
git commit -m "注文処理を修正"
```

コミットメッセージは、

```text
修正
変更
更新
```

だけではなく、何を変更したか分かるようにしてください。

---

## 5. GitHubへPushする

### 初めてそのブランチをPushする場合

```bash
git push -u origin feature-作業名
```

例えば：

```bash
git push -u origin feature-login
```

### 2回目以降

同じブランチであれば、

```bash
git push
```

だけでOKです。

---

# EclipseからGitHubへPushする方法

ターミナルを使わず、EclipseからGit操作を行うこともできます。

---

## 1. 現在のブランチを確認する

Eclipseの「パッケージ・エクスプローラー」でプロジェクト名を確認します。

例えば：

```text
MainPage [JavaDounat feature-mainpage]
```

この場合、

```text
feature-mainpage
```

ブランチで作業しています。

`main`になっている場合は、mainへ直接コミットしないように注意してください。

---

## 2. Eclipseからコミットする

「パッケージ・エクスプローラー」でプロジェクト名を右クリックします。

```text
プロジェクト名を右クリック
↓
チーム
↓
コミット...
```

Gitステージング画面が表示されます。

変更したファイルを

```text
ステージされていない変更
```

から

```text
ステージされた変更
```

へ移動します。

コミットメッセージを入力します。

例：

```text
MainPageを作成
```

```text
ログイン画面を追加
```

```text
注文処理を修正
```

その後、

```text
コミット
```

を押します。

---

## 3. 必要に応じてGitHub側の変更を取り込む

通常、各メンバーがそれぞれ別のfeatureブランチで作業している場合は、自分のブランチを毎回Pullする必要はありません。

ただし、

- 別のPCから同じブランチを変更した
- 他のメンバーが同じブランチを更新した
- Push時に`non-fast-forward`エラーが出た

場合はGitHub側の変更を取り込みます。

Eclipseで、

```text
プロジェクト名を右クリック
↓
チーム
↓
プル...
```

を選択します。

正常に取り込めた場合、

```text
結果：マージ済み
```

などと表示されます。

---

## 4. EclipseからPushする

コミットが完了したら、

```text
プロジェクト名を右クリック
↓
チーム
↓
ブランチのプッシュ 'feature-作業名'...
```

または

```text
プロジェクト名を右クリック
↓
チーム
↓
HEAD のプッシュ...
```

を選択します。

これで現在の作業ブランチがGitHubへPushされます。

---

## EclipseでGitHubへのログインを求められた場合

次のように入力します。

```text
User
→ GitHubのユーザー名

Password
→ 作成したPersonal Access Token
```

GitHubの通常のパスワードは使用しません。

---

# non-fast-forwardエラーが出た場合

Pushした際に、

```text
rejected - non-fast-forward
```

と表示されることがあります。

これは、

```text
GitHub側に
↓
自分のPCへまだ取り込んでいない変更がある
```

という意味です。

この場合、強制Pushはしないでください。

Eclipseでは、

```text
プロジェクト名を右クリック
↓
チーム
↓
プル...
↓
GitHub側の変更を取り込む
↓
必要であればマージ
↓
もう一度Push
```

の順番で操作します。

正常にPullできると、

```text
結果：マージ済み
```

などと表示されます。

その後、

```text
チーム
↓
ブランチのプッシュ 'feature-作業名'...
```

または

```text
チーム
↓
HEAD のプッシュ...
```

を実行します。

> **注意**
>
> `Force Push`や強制Pushは、他の人の変更を消してしまう可能性があります。
>
> 分からない場合は使用しないでください。

---

# Eclipseでの基本的な作業の流れ

```text
作業用ブランチへ移動
↓
コードを編集
↓
コミット
↓
必要ならPull
↓
必要ならマージ
↓
Push
↓
GitHubでPull Requestを作成
↓
チームメンバーまたは管理者が確認
↓
mainへマージ
```

---

# ターミナルとEclipseの対応表

| やりたいこと | ターミナル | Eclipse |
|---|---|---|
| 変更状況確認 | `git status` | Gitステージング |
| 変更内容確認 | `git diff` | Gitステージング |
| コミット | `git commit` | チーム → コミット |
| 最新状態取得 | `git pull` | チーム → プル |
| Push | `git push` | チーム → HEADのプッシュ |
| ブランチ確認 | `git branch` | プロジェクト名横のブランチ表示 |
| ブランチ作成 | `git switch -c` | チーム → 切り替え → 新規ブランチ |

---

# Eclipseを使用する場合の注意

- ファイル単体ではなく、基本的にプロジェクト名を右クリックする。
- `main`へ直接Pushしない。
- 作業用ブランチを使用する。
- `non-fast-forward`が出た場合は強制Pushしない。
- 必要に応じてPullしてGitHub側の変更を取り込む。
- コンフリクトが出た場合は無理に削除しない。
- `.env`、APIキー、パスワード、Personal Access Tokenはコミットしない。

---

# Pull Requestを作る

Pull Requestとは、

```text
自分のブランチで行った変更を
mainへ入れてよいか確認してもらう機能
```

です。

Pushしただけでは`main`には反映されません。

---

## Pull Requestを作成する流れ

1. 自分の作業用ブランチをGitHubへPushする。
2. GitHubでJavaDounatリポジトリを開く。
3. 「Compare & pull request」を押す。
4. `base`と`compare`を確認する。
5. タイトルを書く。
6. 作業内容を書く。
7. 確認してほしい内容を書く。
8. 「Create pull request」を押す。
9. 管理者へ確認をお願いする。

---

## baseとcompare

例えば、

```text
feature-login
```

で作業した場合：

```text
base: main

compare: feature-login
```

となっていることを確認します。

意味：

```text
feature-loginの変更
↓
mainへ入れたい
```

ということです。

---

# feature-loginで作業した場合の例

```bash
git switch feature-login
git add .
git commit -m "ログイン画面を追加"
git push -u origin feature-login
```

その後GitHubを開き、

```text
Compare & pull request
```

を押します。

---

# Pull Requestのタイトル例

```text
ログイン画面を追加
```

---

# Pull Requestの説明例

```md
## 変更内容

- ログイン画面を追加しました
- メールアドレスの入力欄を追加しました
- パスワード入力欄を追加しました

## 確認してほしいこと

- 正しく画面が表示されるか
- 入力欄が正常に動作するか
- レイアウトが崩れていないか
```

入力後、

```text
Create pull request
```

を押します。

---

# Pull Requestを作成した後

Pull Requestを作成したら、管理者やチームメンバーへ確認をお願いします。

```text
自分のブランチ
↓
Pull Request
↓
コード確認
↓
問題なし
↓
mainへマージ
```

> **ルール**
>
> Pull Requestは各メンバーが自分で作成します。
>
> 管理者が確認する前に、自分でmainへマージしないでください。

---

# マージ後に行うこと

Pull Requestがmainへマージされたら、自分のPCのmainも最新状態にします。

```bash
git switch main
git pull origin main
```

次の作業を始める場合は、新しいブランチを作ります。

```bash
git switch -c feature-次の作業名
```

つまり、

```text
Pull Requestがマージされる
↓
mainへ戻る
↓
mainをPull
↓
新しいfeatureブランチを作る
↓
次の作業開始
```

です。

---

# みんなの変更を完成版にまとめる方法

各メンバーは、それぞれ自分の作業ブランチで開発します。

例えば：

```text
feature-login
feature-mainpage
feature-order
feature-user
```

各自が作業を完了したらGitHubへPushします。

```text
各自のブランチ
↓
Push
↓
GitHub
↓
Pull Request
↓
内容確認
↓
Merge
↓
main
```

全員のPull Requestをmainへマージすることで、完成した機能が1つにまとまります。

---

# チーム内のルール

以下のルールを守って作業してください。

- 作業開始前に必ず`main`へ移動する。
- `git pull origin main`でmainを最新状態にする。
- `main`へ直接Pushしない。
- 作業用ブランチを作って作業する。
- ブランチ名は担当する機能が分かる名前にする。
- コミットメッセージには何を変更したか分かる内容を書く。
- Pull Requestは自分で作成する。
- 管理者が確認する前にmainへマージしない。
- `.env`をPushしない。
- パスワードをPushしない。
- APIキーをPushしない。
- Personal Access TokenをPushしない。
- 秘密鍵をPushしない。
- 担当するファイルや機能を事前に共有する。
- 同じファイルの同じ場所を複数人で同時に編集しないようにする。
- 分からないGitエラーが出た場合は無理に操作しない。
- コンフリクトが発生した場合はチームメンバーへ相談する。
- 強制Pushは基本的に使用しない。

---

# よく使うGitコマンド

```bash
git status                         # 変更状況を確認
git branch                         # 現在のブランチ確認
git switch main                    # mainへ移動
git pull origin main               # mainを最新状態にする
git switch -c feature-作業名       # 新しい作業ブランチを作る
git switch feature-作業名          # 既存ブランチへ移動
git add .                          # 変更ファイルを追加
git commit -m "変更内容"           # 変更をコミット
git push -u origin feature-作業名  # 初回Push
git push                           # 2回目以降のPush
```

---

# 作業例

ログイン機能を作成する場合：

```bash
git switch main
git pull origin main
git switch -c feature-login
```

コードを編集します。

その後：

```bash
git status
git add .
git commit -m "ログイン画面を追加"
git push -u origin feature-login
```

GitHubでPull Requestを作成します。

```text
base: main
compare: feature-login
```

管理者に確認してもらい、問題がなければmainへマージします。

マージ後：

```bash
git switch main
git pull origin main
```

次の作業：

```bash
git switch -c feature-次の作業名
```

---

# チーム開発の基本イメージ

```text
main
│
├── feature-login
│      ↓
│    ログイン機能を作成
│      ↓
│    Push
│      ↓
│    Pull Request
│
├── feature-mainpage
│      ↓
│    メインページを作成
│      ↓
│    Push
│      ↓
│    Pull Request
│
└── feature-order
       ↓
     注文機能を作成
       ↓
     Push
       ↓
     Pull Request

        ↓

レビュー

        ↓

mainへマージ

        ↓

完成版
```

---

# 困ったとき

次のようなエラーが出た場合は、無理に操作を続けないでください。

```text
non-fast-forward
```

```text
CONFLICT
```

```text
authentication failed
```

```text
permission denied
```

特に、

```text
Force Push
```

```text
git push --force
```

は、他のメンバーの変更を消してしまう可能性があります。

分からない場合は、スクリーンショットやエラーメッセージを共有してチームで確認してください。
